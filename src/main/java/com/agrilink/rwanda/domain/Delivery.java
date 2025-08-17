package com.agrilink.rwanda.domain;

import com.agrilink.rwanda.domain.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Delivery.
 */
@Entity
@Table(name = "delivery")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "delivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Delivery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pickup_date")
    private Instant pickupDate;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "buyer" }, allowSetters = true)
    private PurchaseOrder order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Delivery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPickupDate() {
        return this.pickupDate;
    }

    public Delivery pickupDate(Instant pickupDate) {
        this.setPickupDate(pickupDate);
        return this;
    }

    public void setPickupDate(Instant pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Instant getDeliveryDate() {
        return this.deliveryDate;
    }

    public Delivery deliveryDate(Instant deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Delivery status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getDriver() {
        return this.driver;
    }

    public void setDriver(User user) {
        this.driver = user;
    }

    public Delivery driver(User user) {
        this.setDriver(user);
        return this;
    }

    public PurchaseOrder getOrder() {
        return this.order;
    }

    public void setOrder(PurchaseOrder purchaseOrder) {
        this.order = purchaseOrder;
    }

    public Delivery order(PurchaseOrder purchaseOrder) {
        this.setOrder(purchaseOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Delivery)) {
            return false;
        }
        return getId() != null && getId().equals(((Delivery) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Delivery{" +
            "id=" + getId() +
            ", pickupDate='" + getPickupDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
