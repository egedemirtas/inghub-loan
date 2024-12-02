package com.inghub.loan.service.impl;

import com.inghub.loan.entity.SysUser;
import com.inghub.loan.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SysUserDetailService implements UserDetailsService {

    @Autowired
    private SysUserRepository sysUserRepository;

    /**
     * Finds user in DB, returns UserDetails equivalent
     * Used for authentication
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<SysUser> user = sysUserRepository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder().username(userObj.getUsername()).password(userObj.getPassword())
                    .roles(userObj.getRole().toString()).build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
