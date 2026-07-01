package com.niveshcore360;

import com.niveshcore360.dto.UserDTO;
import com.niveshcore360.entity.Role;
import com.niveshcore360.entity.User;
import com.niveshcore360.exception.AuthenticationException;
import com.niveshcore360.repository.PortfolioRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.repository.RoleRepository;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.AuditLogService;
import com.niveshcore360.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Service-level unit tests for User authentication and registration.
 */
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserSession userSession;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        Role uRole = Role.builder().name("ROLE_USER").build();
        User user = User.builder()
                .username("samiksha")
                .password("encoded_pass")
                .email("samiksha@gmail.com")
                .roles(Set.of(uRole))
                .build();

        when(userRepository.findByUsername("samiksha")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw_pass", "encoded_pass")).thenReturn(true);

        UserDTO result = authService.login("samiksha", "raw_pass");

        assertNotNull(result);
        assertEquals("samiksha", result.getUsername());
        verify(userSession, times(1)).setCurrentUser(user);
    }

    @Test
    public void testLoginWrongPassword() {
        Role uRole = Role.builder().name("ROLE_USER").build();
        User user = User.builder()
                .username("samiksha")
                .password("encoded_pass")
                .roles(Set.of(uRole))
                .build();

        when(userRepository.findByUsername("samiksha")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "encoded_pass")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.login("samiksha", "wrong_pass"));
    }

    @Test
    public void testRegisterUsernameExists() {
        UserDTO dto = UserDTO.builder()
                .username("samiksha")
                .email("sam@gmail.com")
                .password("pass")
                .build();

        when(userRepository.existsByUsername("samiksha")).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authService.register(dto));
    }
}
