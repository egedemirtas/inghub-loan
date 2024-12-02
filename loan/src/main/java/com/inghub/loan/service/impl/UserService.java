package com.inghub.loan.service.impl;

import com.inghub.loan.dto.UserDto;
import com.inghub.loan.mapper.UserMapper;
import com.inghub.loan.repository.SysUserRepository;
import com.inghub.loan.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.model2Dto(sysUserRepository.save(userMapper.dto2Model(userDto)));
    }
}
