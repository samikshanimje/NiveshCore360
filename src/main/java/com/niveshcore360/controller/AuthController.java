package com.niveshcore360.controller;

import com.niveshcore360.dto.UserDTO;
import com.niveshcore360.exception.NiveshCoreException;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller class coordinating authentication flows between View elements and Service logic.
 */
@Controller
public class AuthController {

    private final AuthService authService;
    private final UserSession userSession;

    @Autowired
    public AuthController(AuthService authService, UserSession userSession) {
        this.authService = authService;
        this.userSession = userSession;
    }

    /**
     * Attempts to register a new user profile.
     */
    public UserDTO register(String username, String email, String fullName, String password) {
        UserDTO dto = UserDTO.builder()
                .username(username)
                .email(email)
                .fullName(fullName)
                .password(password)
                .build();
        return authService.register(dto);
    }

    /**
     * Authenticates a user and starts their session.
     */
    public UserDTO login(String username, String password) {
        return authService.login(username, password);
    }

    /**
     * Checks if the session is active.
     */
    public boolean isLoggedIn() {
        return userSession.isLoggedIn();
    }

    /**
     * Terminates the current active session.
     */
    public void logout() {
        authService.logout();
    }

    /**
     * Triggers password recovery validation and updates password.
     */
    public boolean forgotPassword(String username, String newPassword) {
        return authService.forgotPassword(username, newPassword);
    }

    /**
     * Updates profile values of the logged-in User.
     */
    public UserDTO updateProfile(String email, String fullName, String newPassword) {
        if (!userSession.isLoggedIn()) {
            throw new NiveshCoreException("No active user session found.");
        }
        UserDTO data = UserDTO.builder()
                .email(email)
                .fullName(fullName)
                .password(newPassword)
                .build();
        return authService.updateProfile(userSession.getCurrentUser().getId(), data);
    }
}
