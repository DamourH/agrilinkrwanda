package com.agrilink.rwanda.service.impl;

import com.agrilink.rwanda.domain.FarmerProduce;
import com.agrilink.rwanda.repository.FarmerProduceRepository;
import com.agrilink.rwanda.repository.search.FarmerProduceSearchRepository;
import com.agrilink.rwanda.service.FarmerProduceService;
import com.agrilink.rwanda.service.dto.FarmerProduceDTO;
import com.agrilink.rwanda.service.mapper.FarmerProduceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.agrilink.rwanda.domain.FarmerProduce}.
 */
@Service
@Transactional
public class FarmerProduceServiceImpl implements FarmerProduceService {

    private static final Logger LOG = LoggerFactory.getLogger(FarmerProduceServiceImpl.class);

    private final FarmerProduceRepository farmerProduceRepository;

    private final FarmerProduceMapper farmerProduceMapper;

    private final FarmerProduceSearchRepository farmerProduceSearchRepository;

    public FarmerProduceServiceImpl(
        FarmerProduceRepository farmerProduceRepository,
        FarmerProduceMapper farmerProduceMapper,
        FarmerProduceSearchRepository farmerProduceSearchRepository
    ) {
        this.farmerProduceRepository = farmerProduceRepository;
        this.farmerProduceMapper = farmerProduceMapper;
        this.farmerProduceSearchRepository = farmerProduceSearchRepository;
    }

    @Override
    public FarmerProduceDTO save(FarmerProduceDTO farmerProduceDTO) {
        LOG.debug("Request to save FarmerProduce : {}", farmerProduceDTO);
        FarmerProduce farmerProduce = farmerProduceMapper.toEntity(farmerProduceDTO);
        farmerProduce = farmerProduceRepository.save(farmerProduce);
        farmerProduceSearchRepository.index(farmerProduce);
        return farmerProduceMapper.toDto(farmerProduce);
    }

    @Override
    public FarmerProduceDTO update(FarmerProduceDTO farmerProduceDTO) {
        LOG.debug("Request to update FarmerProduce : {}", farmerProduceDTO);
        FarmerProduce farmerProduce = farmerProduceMapper.toEntity(farmerProduceDTO);
        farmerProduce = farmerProduceRepository.save(farmerProduce);
        farmerProduceSearchRepository.index(farmerProduce);
        return farmerProduceMapper.toDto(farmerProduce);
    }

    @Override
    public Optional<FarmerProduceDTO> partialUpdate(FarmerProduceDTO farmerProduceDTO) {
        LOG.debug("Request to partially update FarmerProduce : {}", farmerProduceDTO);

        return farmerProduceRepository
            .findById(farmerProduceDTO.getId())
            .map(existingFarmerProduce -> {
                farmerProduceMapper.partialUpdate(existingFarmerProduce, farmerProduceDTO);

                return existingFarmerProduce;
            })
            .map(farmerProduceRepository::save)
            .map(savedFarmerProduce -> {
                farmerProduceSearchRepository.index(savedFarmerProduce);
                return savedFarmerProduce;
            })
            .map(farmerProduceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmerProduceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FarmerProduces");
        return farmerProduceRepository.findAll(pageable).map(farmerProduceMapper::toDto);
    }

    public Page<FarmerProduceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return farmerProduceRepository.findAllWithEagerRelationships(pageable).map(farmerProduceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FarmerProduceDTO> findOne(Long id) {
        LOG.debug("Request to get FarmerProduce : {}", id);
        return farmerProduceRepository.findOneWithEagerRelationships(id).map(farmerProduceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FarmerProduce : {}", id);
        farmerProduceRepository.deleteById(id);
        farmerProduceSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmerProduceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of FarmerProduces for query {}", query);
        return farmerProduceSearchRepository.search(query, pageable).map(farmerProduceMapper::toDto);
    }
}
