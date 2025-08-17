package com.agrilink.rwanda.service.impl;

import com.agrilink.rwanda.domain.OrderItem;
import com.agrilink.rwanda.repository.OrderItemRepository;
import com.agrilink.rwanda.repository.search.OrderItemSearchRepository;
import com.agrilink.rwanda.service.OrderItemService;
import com.agrilink.rwanda.service.dto.OrderItemDTO;
import com.agrilink.rwanda.service.mapper.OrderItemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agrilink.rwanda.domain.OrderItem}.
 */
@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    private final OrderItemSearchRepository orderItemSearchRepository;

    public OrderItemServiceImpl(
        OrderItemRepository orderItemRepository,
        OrderItemMapper orderItemMapper,
        OrderItemSearchRepository orderItemSearchRepository
    ) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.orderItemSearchRepository = orderItemSearchRepository;
    }

    @Override
    public OrderItemDTO save(OrderItemDTO orderItemDTO) {
        LOG.debug("Request to save OrderItem : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem = orderItemRepository.save(orderItem);
        orderItemSearchRepository.index(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderItemDTO update(OrderItemDTO orderItemDTO) {
        LOG.debug("Request to update OrderItem : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem = orderItemRepository.save(orderItem);
        orderItemSearchRepository.index(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public Optional<OrderItemDTO> partialUpdate(OrderItemDTO orderItemDTO) {
        LOG.debug("Request to partially update OrderItem : {}", orderItemDTO);

        return orderItemRepository
            .findById(orderItemDTO.getId())
            .map(existingOrderItem -> {
                orderItemMapper.partialUpdate(existingOrderItem, orderItemDTO);

                return existingOrderItem;
            })
            .map(orderItemRepository::save)
            .map(savedOrderItem -> {
                orderItemSearchRepository.index(savedOrderItem);
                return savedOrderItem;
            })
            .map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> findAll() {
        LOG.debug("Request to get all OrderItems");
        return orderItemRepository.findAll().stream().map(orderItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemDTO> findOne(Long id) {
        LOG.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findById(id).map(orderItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete OrderItem : {}", id);
        orderItemRepository.deleteById(id);
        orderItemSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> search(String query) {
        LOG.debug("Request to search OrderItems for query {}", query);
        try {
            return StreamSupport.stream(orderItemSearchRepository.search(query).spliterator(), false).map(orderItemMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
