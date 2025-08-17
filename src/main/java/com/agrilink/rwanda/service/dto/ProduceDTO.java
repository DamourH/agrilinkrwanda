package com.agrilink.rwanda.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.agrilink.rwanda.domain.Produce} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProduceDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private ProductCategoryDTO category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(ProductCategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProduceDTO)) {
            return false;
        }

        ProduceDTO produceDTO = (ProduceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, produceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", category=" + getCategory() +
            "}";
    }
}
