package com.agrilink.rwanda.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.agrilink.rwanda.domain.OrderItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Double quantity;

    @NotNull
    private BigDecimal priceAtOrder;

    private PurchaseOrderDTO order;

    private FarmerProduceDTO farmerProduce;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public PurchaseOrderDTO getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrderDTO order) {
        this.order = order;
    }

    public FarmerProduceDTO getFarmerProduce() {
        return farmerProduce;
    }

    public void setFarmerProduce(FarmerProduceDTO farmerProduce) {
        this.farmerProduce = farmerProduce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemDTO)) {
            return false;
        }

        OrderItemDTO orderItemDTO = (OrderItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", priceAtOrder=" + getPriceAtOrder() +
            ", order=" + getOrder() +
            ", farmerProduce=" + getFarmerProduce() +
            "}";
    }
}
