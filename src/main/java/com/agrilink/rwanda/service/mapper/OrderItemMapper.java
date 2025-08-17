package com.agrilink.rwanda.service.mapper;

import com.agrilink.rwanda.domain.FarmerProduce;
import com.agrilink.rwanda.domain.OrderItem;
import com.agrilink.rwanda.domain.PurchaseOrder;
import com.agrilink.rwanda.service.dto.FarmerProduceDTO;
import com.agrilink.rwanda.service.dto.OrderItemDTO;
import com.agrilink.rwanda.service.dto.PurchaseOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "order", source = "order", qualifiedByName = "purchaseOrderId")
    @Mapping(target = "farmerProduce", source = "farmerProduce", qualifiedByName = "farmerProduceId")
    OrderItemDTO toDto(OrderItem s);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);

    @Named("farmerProduceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FarmerProduceDTO toDtoFarmerProduceId(FarmerProduce farmerProduce);
}
