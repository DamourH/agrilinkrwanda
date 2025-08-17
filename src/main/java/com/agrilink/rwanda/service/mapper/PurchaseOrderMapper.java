package com.agrilink.rwanda.service.mapper;

import com.agrilink.rwanda.domain.PurchaseOrder;
import com.agrilink.rwanda.domain.User;
import com.agrilink.rwanda.service.dto.PurchaseOrderDTO;
import com.agrilink.rwanda.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrder} and its DTO {@link PurchaseOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper extends EntityMapper<PurchaseOrderDTO, PurchaseOrder> {
    @Mapping(target = "buyer", source = "buyer", qualifiedByName = "userLogin")
    PurchaseOrderDTO toDto(PurchaseOrder s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
