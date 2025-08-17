package com.agrilink.rwanda.domain;

import com.agrilink.rwanda.domain.enumeration.QualityGrade;
import com.agrilink.rwanda.domain.enumeration.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A FarmerProduce.
 */
@Entity
@Table(name = "farmer_produce")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "farmerproduce")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FarmerProduce implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Unit unit;

    @NotNull
    @Column(name = "price_per_unit", precision = 21, scale = 2, nullable = false)
    private BigDecimal pricePerUnit;

    @NotNull
    @Column(name = "available_from", nullable = false)
    private Instant availableFrom;

    @Column(name = "available_until")
    private Instant availableUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityGrade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    private User farmer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FarmerProduce id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public FarmerProduce quantity(Double quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return this.unit;
    }

    public FarmerProduce unit(Unit unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getPricePerUnit() {
        return this.pricePerUnit;
    }

    public FarmerProduce pricePerUnit(BigDecimal pricePerUnit) {
        this.setPricePerUnit(pricePerUnit);
        return this;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Instant getAvailableFrom() {
        return this.availableFrom;
    }

    public FarmerProduce availableFrom(Instant availableFrom) {
        this.setAvailableFrom(availableFrom);
        return this;
    }

    public void setAvailableFrom(Instant availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Instant getAvailableUntil() {
        return this.availableUntil;
    }

    public FarmerProduce availableUntil(Instant availableUntil) {
        this.setAvailableUntil(availableUntil);
        return this;
    }

    public void setAvailableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
    }

    public QualityGrade getGrade() {
        return this.grade;
    }

    public FarmerProduce grade(QualityGrade grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(QualityGrade grade) {
        this.grade = grade;
    }

    public User getFarmer() {
        return this.farmer;
    }

    public void setFarmer(User user) {
        this.farmer = user;
    }

    public FarmerProduce farmer(User user) {
        this.setFarmer(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FarmerProduce)) {
            return false;
        }
        return getId() != null && getId().equals(((FarmerProduce) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FarmerProduce{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unit='" + getUnit() + "'" +
            ", pricePerUnit=" + getPricePerUnit() +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", availableUntil='" + getAvailableUntil() + "'" +
            ", grade='" + getGrade() + "'" +
            "}";
    }
}
