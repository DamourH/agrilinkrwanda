package com.agrilink.rwanda.service.mapper;

import static com.agrilink.rwanda.domain.PurchaseOrderAsserts.*;
import static com.agrilink.rwanda.domain.PurchaseOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderMapperTest {

    private PurchaseOrderMapper purchaseOrderMapper;

    @BeforeEach
    void setUp() {
        purchaseOrderMapper = new PurchaseOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchaseOrderSample1();
        var actual = purchaseOrderMapper.toEntity(purchaseOrderMapper.toDto(expected));
        assertPurchaseOrderAllPropertiesEquals(expected, actual);
    }
}
