package com.agrilink.rwanda.service.mapper;

import com.agrilink.rwanda.domain.Produce;
import com.agrilink.rwanda.domain.ProductCategory;
import com.agrilink.rwanda.service.dto.ProduceDTO;
import com.agrilink.rwanda.service.dto.ProductCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Produce} and its DTO {@link ProduceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProduceMapper extends EntityMapper<ProduceDTO, Produce> {
    @Mapping(target = "category", source = "category", qualifiedByName = "productCategoryId")
    ProduceDTO toDto(Produce s);

    @Named("productCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductCategoryDTO toDtoProductCategoryId(ProductCategory productCategory);
}
