package com.agrilink.rwanda.service.dto;

import com.agrilink.rwanda.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.agrilink.rwanda.domain.Delivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryDTO implements Serializable {

    private Long id;

    private Instant pickupDate;

    private Instant deliveryDate;

    private OrderStatus status;

    private UserDTO driver;

    private PurchaseOrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Instant pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Instant getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public UserDTO getDriver() {
        return driver;
    }

    public void setDriver(UserDTO driver) {
        this.driver = driver;
    }

    public PurchaseOrderDTO getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryDTO)) {
            return false;
        }

        DeliveryDTO deliveryDTO = (DeliveryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliveryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryDTO{" +
            "id=" + getId() +
            ", pickupDate='" + getPickupDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", driver=" + getDriver() +
            ", order=" + getOrder() +
            "}";
    }
}
