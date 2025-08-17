package com.agrilink.rwanda.service.mapper;

import static com.agrilink.rwanda.domain.FarmerProduceAsserts.*;
import static com.agrilink.rwanda.domain.FarmerProduceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FarmerProduceMapperTest {

    private FarmerProduceMapper farmerProduceMapper;

    @BeforeEach
    void setUp() {
        farmerProduceMapper = new FarmerProduceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFarmerProduceSample1();
        var actual = farmerProduceMapper.toEntity(farmerProduceMapper.toDto(expected));
        assertFarmerProduceAllPropertiesEquals(expected, actual);
    }
}
