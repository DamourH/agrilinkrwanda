package com.agrilink.rwanda.service.impl;

import com.agrilink.rwanda.domain.Delivery;
import com.agrilink.rwanda.repository.DeliveryRepository;
import com.agrilink.rwanda.repository.search.DeliverySearchRepository;
import com.agrilink.rwanda.service.DeliveryService;
import com.agrilink.rwanda.service.dto.DeliveryDTO;
import com.agrilink.rwanda.service.mapper.DeliveryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agrilink.rwanda.domain.Delivery}.
 */
@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    private final DeliverySearchRepository deliverySearchRepository;

    public DeliveryServiceImpl(
        DeliveryRepository deliveryRepository,
        DeliveryMapper deliveryMapper,
        DeliverySearchRepository deliverySearchRepository
    ) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.deliverySearchRepository = deliverySearchRepository;
    }

    @Override
    public DeliveryDTO save(DeliveryDTO deliveryDTO) {
        LOG.debug("Request to save Delivery : {}", deliveryDTO);
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        deliverySearchRepository.index(delivery);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public DeliveryDTO update(DeliveryDTO deliveryDTO) {
        LOG.debug("Request to update Delivery : {}", deliveryDTO);
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        deliverySearchRepository.index(delivery);
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public Optional<DeliveryDTO> partialUpdate(DeliveryDTO deliveryDTO) {
        LOG.debug("Request to partially update Delivery : {}", deliveryDTO);

        return deliveryRepository
            .findById(deliveryDTO.getId())
            .map(existingDelivery -> {
                deliveryMapper.partialUpdate(existingDelivery, deliveryDTO);

                return existingDelivery;
            })
            .map(deliveryRepository::save)
            .map(savedDelivery -> {
                deliverySearchRepository.index(savedDelivery);
                return savedDelivery;
            })
            .map(deliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDTO> findAll() {
        LOG.debug("Request to get all Deliveries");
        return deliveryRepository.findAll().stream().map(deliveryMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<DeliveryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return deliveryRepository.findAllWithEagerRelationships(pageable).map(deliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryDTO> findOne(Long id) {
        LOG.debug("Request to get Delivery : {}", id);
        return deliveryRepository.findOneWithEagerRelationships(id).map(deliveryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Delivery : {}", id);
        deliveryRepository.deleteById(id);
        deliverySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDTO> search(String query) {
        LOG.debug("Request to search Deliveries for query {}", query);
        try {
            return StreamSupport.stream(deliverySearchRepository.search(query).spliterator(), false).map(deliveryMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
