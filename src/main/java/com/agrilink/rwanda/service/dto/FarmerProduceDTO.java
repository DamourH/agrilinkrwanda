package com.agrilink.rwanda.service.dto;

import com.agrilink.rwanda.domain.enumeration.QualityGrade;
import com.agrilink.rwanda.domain.enumeration.Unit;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.agrilink.rwanda.domain.FarmerProduce} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FarmerProduceDTO implements Serializable {

    private Long id;

    @NotNull
    private Double quantity;

    @NotNull
    private Unit unit;

    @NotNull
    private BigDecimal pricePerUnit;

    @NotNull
    private Instant availableFrom;

    private Instant availableUntil;

    private QualityGrade grade;

    private UserDTO farmer;

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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Instant getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Instant availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Instant getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
    }

    public QualityGrade getGrade() {
        return grade;
    }

    public void setGrade(QualityGrade grade) {
        this.grade = grade;
    }

    public UserDTO getFarmer() {
        return farmer;
    }

    public void setFarmer(UserDTO farmer) {
        this.farmer = farmer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FarmerProduceDTO)) {
            return false;
        }

        FarmerProduceDTO farmerProduceDTO = (FarmerProduceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, farmerProduceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FarmerProduceDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unit='" + getUnit() + "'" +
            ", pricePerUnit=" + getPricePerUnit() +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", availableUntil='" + getAvailableUntil() + "'" +
            ", grade='" + getGrade() + "'" +
            ", farmer=" + getFarmer() +
            "}";
    }
}
