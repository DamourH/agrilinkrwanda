package com.agrilink.rwanda.service.impl;

import com.agrilink.rwanda.domain.Produce;
import com.agrilink.rwanda.repository.ProduceRepository;
import com.agrilink.rwanda.repository.search.ProduceSearchRepository;
import com.agrilink.rwanda.service.ProduceService;
import com.agrilink.rwanda.service.dto.ProduceDTO;
import com.agrilink.rwanda.service.mapper.ProduceMapper;
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
 * Service Implementation for managing {@link com.agrilink.rwanda.domain.Produce}.
 */
@Service
@Transactional
public class ProduceServiceImpl implements ProduceService {

    private static final Logger LOG = LoggerFactory.getLogger(ProduceServiceImpl.class);

    private final ProduceRepository produceRepository;

    private final ProduceMapper produceMapper;

    private final ProduceSearchRepository produceSearchRepository;

    public ProduceServiceImpl(
        ProduceRepository produceRepository,
        ProduceMapper produceMapper,
        ProduceSearchRepository produceSearchRepository
    ) {
        this.produceRepository = produceRepository;
        this.produceMapper = produceMapper;
        this.produceSearchRepository = produceSearchRepository;
    }

    @Override
    public ProduceDTO save(ProduceDTO produceDTO) {
        LOG.debug("Request to save Produce : {}", produceDTO);
        Produce produce = produceMapper.toEntity(produceDTO);
        produce = produceRepository.save(produce);
        produceSearchRepository.index(produce);
        return produceMapper.toDto(produce);
    }

    @Override
    public ProduceDTO update(ProduceDTO produceDTO) {
        LOG.debug("Request to update Produce : {}", produceDTO);
        Produce produce = produceMapper.toEntity(produceDTO);
        produce = produceRepository.save(produce);
        produceSearchRepository.index(produce);
        return produceMapper.toDto(produce);
    }

    @Override
    public Optional<ProduceDTO> partialUpdate(ProduceDTO produceDTO) {
        LOG.debug("Request to partially update Produce : {}", produceDTO);

        return produceRepository
            .findById(produceDTO.getId())
            .map(existingProduce -> {
                produceMapper.partialUpdate(existingProduce, produceDTO);

                return existingProduce;
            })
            .map(produceRepository::save)
            .map(savedProduce -> {
                produceSearchRepository.index(savedProduce);
                return savedProduce;
            })
            .map(produceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduceDTO> findAll() {
        LOG.debug("Request to get all Produces");
        return produceRepository.findAll().stream().map(produceMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProduceDTO> findOne(Long id) {
        LOG.debug("Request to get Produce : {}", id);
        return produceRepository.findById(id).map(produceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Produce : {}", id);
        produceRepository.deleteById(id);
        produceSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduceDTO> search(String query) {
        LOG.debug("Request to search Produces for query {}", query);
        try {
            return StreamSupport.stream(produceSearchRepository.search(query).spliterator(), false).map(produceMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
