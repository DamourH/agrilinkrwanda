package com.agrilink.rwanda.domain;

import static com.agrilink.rwanda.domain.PurchaseOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseOrder.class);
        PurchaseOrder purchaseOrder1 = getPurchaseOrderSample1();
        PurchaseOrder purchaseOrder2 = new PurchaseOrder();
        assertThat(purchaseOrder1).isNotEqualTo(purchaseOrder2);

        purchaseOrder2.setId(purchaseOrder1.getId());
        assertThat(purchaseOrder1).isEqualTo(purchaseOrder2);

        purchaseOrder2 = getPurchaseOrderSample2();
        assertThat(purchaseOrder1).isNotEqualTo(purchaseOrder2);
    }
}
