package com.agrilink.rwanda.domain;

import static com.agrilink.rwanda.domain.FarmerProduceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FarmerProduceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FarmerProduce.class);
        FarmerProduce farmerProduce1 = getFarmerProduceSample1();
        FarmerProduce farmerProduce2 = new FarmerProduce();
        assertThat(farmerProduce1).isNotEqualTo(farmerProduce2);

        farmerProduce2.setId(farmerProduce1.getId());
        assertThat(farmerProduce1).isEqualTo(farmerProduce2);

        farmerProduce2 = getFarmerProduceSample2();
        assertThat(farmerProduce1).isNotEqualTo(farmerProduce2);
    }
}
