package com.inghub.loan.dto;

import com.inghub.loan.util.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseUserDto {
    @NotBlank(message = "{loan.request.field.error.empty}")
    private String username;
    @NotBlank(message = "{loan.request.field.error.empty}")
    private String password;
    private Role role;
}
