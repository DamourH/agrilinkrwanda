package com.agrilink.rwanda.service.impl;

import com.agrilink.rwanda.domain.PurchaseOrder;
import com.agrilink.rwanda.repository.PurchaseOrderRepository;
import com.agrilink.rwanda.repository.search.PurchaseOrderSearchRepository;
import com.agrilink.rwanda.service.PurchaseOrderService;
import com.agrilink.rwanda.service.dto.PurchaseOrderDTO;
import com.agrilink.rwanda.service.mapper.PurchaseOrderMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agrilink.rwanda.domain.PurchaseOrder}.
 */
@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;

    private final PurchaseOrderSearchRepository purchaseOrderSearchRepository;

    public PurchaseOrderServiceImpl(
        PurchaseOrderRepository purchaseOrderRepository,
        PurchaseOrderMapper purchaseOrderMapper,
        PurchaseOrderSearchRepository purchaseOrderSearchRepository
    ) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderSearchRepository = purchaseOrderSearchRepository;
    }

    @Override
    public PurchaseOrderDTO save(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to save PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        purchaseOrderSearchRepository.index(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    public PurchaseOrderDTO update(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to update PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        purchaseOrderSearchRepository.index(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    public Optional<PurchaseOrderDTO> partialUpdate(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to partially update PurchaseOrder : {}", purchaseOrderDTO);

        return purchaseOrderRepository
            .findById(purchaseOrderDTO.getId())
            .map(existingPurchaseOrder -> {
                purchaseOrderMapper.partialUpdate(existingPurchaseOrder, purchaseOrderDTO);

                return existingPurchaseOrder;
            })
            .map(purchaseOrderRepository::save)
            .map(savedPurchaseOrder -> {
                purchaseOrderSearchRepository.index(savedPurchaseOrder);
                return savedPurchaseOrder;
            })
            .map(purchaseOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PurchaseOrders");
        return purchaseOrderRepository.findAll(pageable).map(purchaseOrderMapper::toDto);
    }

    public Page<PurchaseOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return purchaseOrderRepository.findAllWithEagerRelationships(pageable).map(purchaseOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderDTO> findOne(Long id) {
        LOG.debug("Request to get PurchaseOrder : {}", id);
        return purchaseOrderRepository.findOneWithEagerRelationships(id).map(purchaseOrderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PurchaseOrder : {}", id);
        purchaseOrderRepository.deleteById(id);
        purchaseOrderSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PurchaseOrders for query {}", query);
        return purchaseOrderSearchRepository.search(query, pageable).map(purchaseOrderMapper::toDto);
    }
}
