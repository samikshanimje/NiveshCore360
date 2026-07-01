package com.niveshcore360.dto;

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
    private String role;
}
