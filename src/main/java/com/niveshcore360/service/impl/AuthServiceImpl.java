package com.niveshcore360.service.impl;

import com.niveshcore360.dto.UserDTO;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.entity.Role;
import com.niveshcore360.entity.User;
import com.niveshcore360.exception.AuthenticationException;
import com.niveshcore360.exception.ResourceNotFoundException;
import com.niveshcore360.mapper.AppMapper;
import com.niveshcore360.repository.PortfolioRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.AuthService;
import com.niveshcore360.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation managing user registration, authentication, and session logic.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSession userSession;
    private final AuditLogService auditLogService;
    private final com.niveshcore360.repository.RoleRepository roleRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PortfolioRepository portfolioRepository,
                           PasswordEncoder passwordEncoder,
                           UserSession userSession,
                           AuditLogService auditLogService,
                           com.niveshcore360.repository.RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSession = userSession;
        this.auditLogService = auditLogService;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDTO register(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new AuthenticationException("Username is already taken.");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new AuthenticationException("Email is already registered.");
        }

        String roleName = userDTO.getRole() != null ? userDTO.getRole() : "ROLE_USER";
        com.niveshcore360.entity.Role dbRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AuthenticationException("Requested security role does not exist: " + roleName));

        java.util.Set<com.niveshcore360.entity.Role> userRoles = new java.util.HashSet<>();
        userRoles.add(dbRole);

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(userRoles)
                .build();

        User savedUser = userRepository.save(user);

        // Auto-create a default portfolio for new users
        Portfolio defaultPortfolio = Portfolio.builder()
                .name("Default Portfolio")
                .user(savedUser)
                .build();
        portfolioRepository.save(defaultPortfolio);

        auditLogService.log(savedUser, "REGISTER", "User registered successfully, default portfolio created.");

        return AppMapper.toUserDTO(savedUser);
    }

    @Override
    public UserDTO login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid username or password.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        userSession.setCurrentUser(user);
        auditLogService.log(user, "LOGIN", "User logged in successfully.");

        return AppMapper.toUserDTO(user);
    }

    @Override
    public void logout() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            auditLogService.log(currentUser, "LOGOUT", "User logged out.");
        }
        userSession.logout();
    }

    @Override
    public boolean forgotPassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("Username not found.");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogService.log(user, "FORGOT_PASSWORD", "Password reset requested and updated successfully.");
        return true;
    }

    @Override
    public UserDTO updateProfile(Long userId, UserDTO profileData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validation checks
        if (!user.getEmail().equalsIgnoreCase(profileData.getEmail()) && userRepository.existsByEmail(profileData.getEmail())) {
            throw new AuthenticationException("Email is already registered by another account.");
        }

        user.setFullName(profileData.getFullName());
        user.setEmail(profileData.getEmail());
        
        if (profileData.getPassword() != null && !profileData.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(profileData.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        // Keep session updated with the latest user object
        if (userSession.getCurrentUser() != null && userSession.getCurrentUser().getId().equals(userId)) {
            userSession.setCurrentUser(updatedUser);
        }

        auditLogService.log(updatedUser, "PROFILE_UPDATE", "User updated profile details.");

        return AppMapper.toUserDTO(updatedUser);
    }
}
