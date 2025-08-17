package com.agrilink.rwanda.service.mapper;

import com.agrilink.rwanda.domain.FarmerProduce;
import com.agrilink.rwanda.domain.User;
import com.agrilink.rwanda.service.dto.FarmerProduceDTO;
import com.agrilink.rwanda.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FarmerProduce} and its DTO {@link FarmerProduceDTO}.
 */
@Mapper(componentModel = "spring")
public interface FarmerProduceMapper extends EntityMapper<FarmerProduceDTO, FarmerProduce> {
    @Mapping(target = "farmer", source = "farmer", qualifiedByName = "userLogin")
    FarmerProduceDTO toDto(FarmerProduce s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
