package com.agrilink.rwanda.domain;

import static com.agrilink.rwanda.domain.DeliveryTestSamples.*;
import static com.agrilink.rwanda.domain.PurchaseOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Delivery.class);
        Delivery delivery1 = getDeliverySample1();
        Delivery delivery2 = new Delivery();
        assertThat(delivery1).isNotEqualTo(delivery2);

        delivery2.setId(delivery1.getId());
        assertThat(delivery1).isEqualTo(delivery2);

        delivery2 = getDeliverySample2();
        assertThat(delivery1).isNotEqualTo(delivery2);
    }

    @Test
    void orderTest() {
        Delivery delivery = getDeliveryRandomSampleGenerator();
        PurchaseOrder purchaseOrderBack = getPurchaseOrderRandomSampleGenerator();

        delivery.setOrder(purchaseOrderBack);
        assertThat(delivery.getOrder()).isEqualTo(purchaseOrderBack);

        delivery.order(null);
        assertThat(delivery.getOrder()).isNull();
    }
}
