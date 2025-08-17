package com.agrilink.rwanda.domain;

import static com.agrilink.rwanda.domain.ProduceTestSamples.*;
import static com.agrilink.rwanda.domain.ProductCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProduceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Produce.class);
        Produce produce1 = getProduceSample1();
        Produce produce2 = new Produce();
        assertThat(produce1).isNotEqualTo(produce2);

        produce2.setId(produce1.getId());
        assertThat(produce1).isEqualTo(produce2);

        produce2 = getProduceSample2();
        assertThat(produce1).isNotEqualTo(produce2);
    }

    @Test
    void categoryTest() {
        Produce produce = getProduceRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        produce.setCategory(productCategoryBack);
        assertThat(produce.getCategory()).isEqualTo(productCategoryBack);

        produce.category(null);
        assertThat(produce.getCategory()).isNull();
    }
}
