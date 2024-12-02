package com.inghub.loan.mapper;

import com.inghub.loan.dto.UserDto;
import com.inghub.loan.entity.SysUser;
import com.inghub.loan.request.UserRequest;
import com.inghub.loan.response.UserResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {

    UserDto request2Dto(UserRequest user);

    SysUser dto2Model(UserDto userDto);

    UserDto model2Dto(SysUser userDto);

    UserResponse dto2Response(UserDto userDto);
}
