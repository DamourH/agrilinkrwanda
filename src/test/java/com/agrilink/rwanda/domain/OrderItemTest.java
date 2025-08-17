package com.agrilink.rwanda.domain;

import static com.agrilink.rwanda.domain.FarmerProduceTestSamples.*;
import static com.agrilink.rwanda.domain.OrderItemTestSamples.*;
import static com.agrilink.rwanda.domain.PurchaseOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agrilink.rwanda.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        PurchaseOrder purchaseOrderBack = getPurchaseOrderRandomSampleGenerator();

        orderItem.setOrder(purchaseOrderBack);
        assertThat(orderItem.getOrder()).isEqualTo(purchaseOrderBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }

    @Test
    void farmerProduceTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        FarmerProduce farmerProduceBack = getFarmerProduceRandomSampleGenerator();

        orderItem.setFarmerProduce(farmerProduceBack);
        assertThat(orderItem.getFarmerProduce()).isEqualTo(farmerProduceBack);

        orderItem.farmerProduce(null);
        assertThat(orderItem.getFarmerProduce()).isNull();
    }
}
