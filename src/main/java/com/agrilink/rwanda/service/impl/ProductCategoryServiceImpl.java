package com.agrilink.rwanda.service.impl;

import com.agrilink.rwanda.domain.ProductCategory;
import com.agrilink.rwanda.repository.ProductCategoryRepository;
import com.agrilink.rwanda.repository.search.ProductCategorySearchRepository;
import com.agrilink.rwanda.service.ProductCategoryService;
import com.agrilink.rwanda.service.dto.ProductCategoryDTO;
import com.agrilink.rwanda.service.mapper.ProductCategoryMapper;
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
 * Service Implementation for managing {@link com.agrilink.rwanda.domain.ProductCategory}.
 */
@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductCategorySearchRepository productCategorySearchRepository;

    public ProductCategoryServiceImpl(
        ProductCategoryRepository productCategoryRepository,
        ProductCategoryMapper productCategoryMapper,
        ProductCategorySearchRepository productCategorySearchRepository
    ) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.productCategorySearchRepository = productCategorySearchRepository;
    }

    @Override
    public ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO) {
        LOG.debug("Request to save ProductCategory : {}", productCategoryDTO);
        ProductCategory productCategory = productCategoryMapper.toEntity(productCategoryDTO);
        productCategory = productCategoryRepository.save(productCategory);
        productCategorySearchRepository.index(productCategory);
        return productCategoryMapper.toDto(productCategory);
    }

    @Override
    public ProductCategoryDTO update(ProductCategoryDTO productCategoryDTO) {
        LOG.debug("Request to update ProductCategory : {}", productCategoryDTO);
        ProductCategory productCategory = productCategoryMapper.toEntity(productCategoryDTO);
        productCategory = productCategoryRepository.save(productCategory);
        productCategorySearchRepository.index(productCategory);
        return productCategoryMapper.toDto(productCategory);
    }

    @Override
    public Optional<ProductCategoryDTO> partialUpdate(ProductCategoryDTO productCategoryDTO) {
        LOG.debug("Request to partially update ProductCategory : {}", productCategoryDTO);

        return productCategoryRepository
            .findById(productCategoryDTO.getId())
            .map(existingProductCategory -> {
                productCategoryMapper.partialUpdate(existingProductCategory, productCategoryDTO);

                return existingProductCategory;
            })
            .map(productCategoryRepository::save)
            .map(savedProductCategory -> {
                productCategorySearchRepository.index(savedProductCategory);
                return savedProductCategory;
            })
            .map(productCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findAll() {
        LOG.debug("Request to get all ProductCategories");
        return productCategoryRepository
            .findAll()
            .stream()
            .map(productCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get ProductCategory : {}", id);
        return productCategoryRepository.findById(id).map(productCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProductCategory : {}", id);
        productCategoryRepository.deleteById(id);
        productCategorySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> search(String query) {
        LOG.debug("Request to search ProductCategories for query {}", query);
        try {
            return StreamSupport.stream(productCategorySearchRepository.search(query).spliterator(), false)
                .map(productCategoryMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
