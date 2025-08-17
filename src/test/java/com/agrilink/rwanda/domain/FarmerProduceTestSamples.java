package com.agrilink.rwanda.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FarmerProduceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static FarmerProduce getFarmerProduceSample1() {
        return new FarmerProduce().id(1L);
    }

    public static FarmerProduce getFarmerProduceSample2() {
        return new FarmerProduce().id(2L);
    }

    public static FarmerProduce getFarmerProduceRandomSampleGenerator() {
        return new FarmerProduce().id(longCount.incrementAndGet());
    }
}
