package com.agrilink.rwanda.service.mapper;

import static com.agrilink.rwanda.domain.ProduceAsserts.*;
import static com.agrilink.rwanda.domain.ProduceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProduceMapperTest {

    private ProduceMapper produceMapper;

    @BeforeEach
    void setUp() {
        produceMapper = new ProduceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProduceSample1();
        var actual = produceMapper.toEntity(produceMapper.toDto(expected));
        assertProduceAllPropertiesEquals(expected, actual);
    }
}
