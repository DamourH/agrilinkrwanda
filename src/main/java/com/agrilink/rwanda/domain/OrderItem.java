package com.agrilink.rwanda.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A OrderItem.
 */
@Entity
@Table(name = "order_item")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orderitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @NotNull
    @Column(name = "price_at_order", precision = 21, scale = 2, nullable = false)
    private BigDecimal priceAtOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "buyer" }, allowSetters = true)
    private PurchaseOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "farmer" }, allowSetters = true)
    private FarmerProduce farmerProduce;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public OrderItem quantity(Double quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtOrder() {
        return this.priceAtOrder;
    }

    public OrderItem priceAtOrder(BigDecimal priceAtOrder) {
        this.setPriceAtOrder(priceAtOrder);
        return this;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public PurchaseOrder getOrder() {
        return this.order;
    }

    public void setOrder(PurchaseOrder purchaseOrder) {
        this.order = purchaseOrder;
    }

    public OrderItem order(PurchaseOrder purchaseOrder) {
        this.setOrder(purchaseOrder);
        return this;
    }

    public FarmerProduce getFarmerProduce() {
        return this.farmerProduce;
    }

    public void setFarmerProduce(FarmerProduce farmerProduce) {
        this.farmerProduce = farmerProduce;
    }

    public OrderItem farmerProduce(FarmerProduce farmerProduce) {
        this.setFarmerProduce(farmerProduce);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", priceAtOrder=" + getPriceAtOrder() +
            "}";
    }
}
