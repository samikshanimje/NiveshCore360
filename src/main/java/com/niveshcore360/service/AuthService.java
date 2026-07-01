package com.niveshcore360.service;

import com.niveshcore360.dto.UserDTO;

/**
 * Service interface for User Authentication and Profile updates.
 */
public interface AuthService {
    UserDTO register(UserDTO userDTO);
    UserDTO login(String username, String password);
    void logout();
    boolean forgotPassword(String username, String newPassword);
    UserDTO updateProfile(Long userId, UserDTO profileData);
}
