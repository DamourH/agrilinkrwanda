package com.agrilink.rwanda.service.mapper;

import com.agrilink.rwanda.domain.Delivery;
import com.agrilink.rwanda.domain.PurchaseOrder;
import com.agrilink.rwanda.domain.User;
import com.agrilink.rwanda.service.dto.DeliveryDTO;
import com.agrilink.rwanda.service.dto.PurchaseOrderDTO;
import com.agrilink.rwanda.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Delivery} and its DTO {@link DeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper extends EntityMapper<DeliveryDTO, Delivery> {
    @Mapping(target = "driver", source = "driver", qualifiedByName = "userLogin")
    @Mapping(target = "order", source = "order", qualifiedByName = "purchaseOrderId")
    DeliveryDTO toDto(Delivery s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
