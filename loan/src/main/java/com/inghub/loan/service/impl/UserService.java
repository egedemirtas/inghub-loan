package com.inghub.loan.service.impl;

import com.inghub.loan.dto.UserDto;
import com.inghub.loan.entity.SysUser;
import com.inghub.loan.mapper.UserMapper;
import com.inghub.loan.repository.SysUserRepository;
import com.inghub.loan.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanService.class);

    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        SysUser savedUser = sysUserRepository.save(userMapper.dto2Model(userDto));
        LOGGER.info("User is created, id={}", savedUser.getId());
        return userMapper.model2Dto(savedUser);
    }
}
