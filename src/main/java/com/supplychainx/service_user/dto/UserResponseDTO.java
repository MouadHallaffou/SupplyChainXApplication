package com.supplychainx.service_user.dto;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isActive;
    private Boolean isDeleted;
    private String roleName;

}
