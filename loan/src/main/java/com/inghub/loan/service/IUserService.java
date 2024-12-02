package com.inghub.loan.service;

import com.inghub.loan.dto.UserDto;

public interface IUserService {

    /**
     * Creates user with userDto details and encrypts password
     * @param userDto
     * @return UserDto
     */
    UserDto createUser(UserDto userDto);
}
