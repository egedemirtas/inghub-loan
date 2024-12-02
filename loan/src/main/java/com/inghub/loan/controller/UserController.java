package com.inghub.loan.controller;

import com.inghub.loan.dto.UserDto;
import com.inghub.loan.mapper.UserMapper;
import com.inghub.loan.request.UserRequest;
import com.inghub.loan.response.UserResponse;
import com.inghub.loan.service.IUserService;
import com.inghub.loan.service.impl.SysUserDetailService;
import com.inghub.loan.webToken.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "Create user")
@RestController
@RequestMapping(path = "/user", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SysUserDetailService sysUserDetailService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest user) {
        UserDto createdUser = userService.createUser(userMapper.request2Dto(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.dto2Response(createdUser));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody UserRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            String token =
                    jwtService.generateToken(sysUserDetailService.loadUserByUsername(user.getUsername()));
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        }
        throw new UsernameNotFoundException("User not authenticated.");
    }
}
