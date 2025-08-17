package com.agrilink.rwanda.service.mapper;

import static com.agrilink.rwanda.domain.ProductCategoryAsserts.*;
import static com.agrilink.rwanda.domain.ProductCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductCategoryMapperTest {

    private ProductCategoryMapper productCategoryMapper;

    @BeforeEach
    void setUp() {
        productCategoryMapper = new ProductCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductCategorySample1();
        var actual = productCategoryMapper.toEntity(productCategoryMapper.toDto(expected));
        assertProductCategoryAllPropertiesEquals(expected, actual);
    }
}
