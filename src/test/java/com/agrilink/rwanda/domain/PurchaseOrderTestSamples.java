package com.agrilink.rwanda.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PurchaseOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PurchaseOrder getPurchaseOrderSample1() {
        return new PurchaseOrder().id(1L).deliveryAddress("deliveryAddress1").notes("notes1");
    }

    public static PurchaseOrder getPurchaseOrderSample2() {
        return new PurchaseOrder().id(2L).deliveryAddress("deliveryAddress2").notes("notes2");
    }

    public static PurchaseOrder getPurchaseOrderRandomSampleGenerator() {
        return new PurchaseOrder()
            .id(longCount.incrementAndGet())
            .deliveryAddress(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
