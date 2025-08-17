package com.agrilink.rwanda.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProduceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Produce getProduceSample1() {
        return new Produce().id(1L).name("name1").description("description1");
    }

    public static Produce getProduceSample2() {
        return new Produce().id(2L).name("name2").description("description2");
    }

    public static Produce getProduceRandomSampleGenerator() {
        return new Produce().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
