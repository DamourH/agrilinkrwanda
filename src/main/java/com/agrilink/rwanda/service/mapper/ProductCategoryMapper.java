package com.agrilink.rwanda.service.mapper;

import com.agrilink.rwanda.domain.ProductCategory;
import com.agrilink.rwanda.service.dto.ProductCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductCategory} and its DTO {@link ProductCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductCategoryMapper extends EntityMapper<ProductCategoryDTO, ProductCategory> {}
