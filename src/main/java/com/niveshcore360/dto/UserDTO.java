package com.niveshcore360.dto;

import com.niveshcore360.entity.Role;
import lombok.*;

/**
 * Data Transfer Object for User profile and registration info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String password;
    private Role role;
}
