package com.module.driveronboardingservice.service;

import com.module.driveronboardingservice.constants.DriverOnBoardingConstants;
import com.module.driveronboardingservice.dao.Role;
import com.module.driveronboardingservice.dao.User;
import com.module.driveronboardingservice.dto.UserRegistration;
import com.module.driveronboardingservice.exception.DriverOnBoardingException;
import com.module.driveronboardingservice.repository.RoleRepository;
import com.module.driveronboardingservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DriverService driverService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_NonExistingRole() throws DriverOnBoardingException {
        // Prepare input data
        UserRegistration registrationDto = new UserRegistration();
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
        registrationDto.setIsDriver(true);

        // Mock the behavior of roleRepository.findByName() to return null (non-existing role)
        when(roleRepository.findByName(anyString())).thenReturn(null);

        // Mock the behavior of roleRepository.save()
        Role mockRole = new Role();
        when(roleRepository.save(any(Role.class))).thenReturn(mockRole);

        // Mock the behavior of driverService.saveDriverDetails()
        doNothing().when(driverService).saveDriverDetails(any(UserRegistration.class));

        // Mock the behavior of userRepository.save()
        User mockUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Mock the behavior of passwordEncoder.encode()
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        // Perform the test
        User result = assertDoesNotThrow(() -> userService.save(registrationDto));

        // Verify that roleRepository.findByName() is called
        verify(roleRepository, times(1)).findByName(DriverOnBoardingConstants.DRIVER_ROLE);

        // Verify that roleRepository.save() is called
        verify(roleRepository, times(1)).save(any(Role.class));

        // Verify that driverService.saveDriverDetails() is called
        verify(driverService, times(1)).saveDriverDetails(registrationDto);

        // Verify that userRepository.save() is called
        verify(userRepository, times(1)).save(any(User.class));

        // Verify that passwordEncoder.encode() is called
        verify(passwordEncoder, times(1)).encode("password");

        // Assert the result
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void testSave_ExistingRole() throws DriverOnBoardingException {
        // Prepare input data
        UserRegistration registrationDto = new UserRegistration();
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
        registrationDto.setIsDriver(true);

        // Mock the behavior of roleRepository.findByName() to return an existing role
        Role mockRole = new Role();
        when(roleRepository.findByName(anyString())).thenReturn(mockRole);

        // Mock the behavior of driverService.saveDriverDetails()
        doNothing().when(driverService).saveDriverDetails(any(UserRegistration.class));

        // Mock the behavior of userRepository.save()
        User mockUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Mock the behavior of passwordEncoder.encode()
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        // Perform the test
        User result = assertDoesNotThrow(() -> userService.save(registrationDto));

        // Verify that roleRepository.findByName() is called
        verify(roleRepository, times(1)).findByName(DriverOnBoardingConstants.DRIVER_ROLE);

        // Verify that roleRepository.save() is not called (existing role)
        verify(roleRepository, never()).save(any(Role.class));

        // Verify that driverService.saveDriverDetails() is called
        verify(driverService, times(1)).saveDriverDetails(registrationDto);

        // Verify that userRepository.save() is called
        verify(userRepository, times(1)).save(any(User.class));

        // Verify that passwordEncoder.encode() is called
        verify(passwordEncoder, times(1)).encode("password");

        // Assert the result
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void testFindUserByEmail() {
        // Prepare input data
        String testEmail = "test@example.com";

        // Mock the behavior of userRepository.findByEmail()
        User mockUser = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser);

        // Perform the test
        User result = userService.findUserByEmail(testEmail);

        // Verify that userRepository.findByEmail() is called
        verify(userRepository, times(1)).findByEmail(testEmail);

        // Assert the result
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void testFindUserByEmail_NonExistingUser() {
        // Prepare input data
        String testEmail = "test@example.com";

        // Mock the behavior of userRepository.findByEmail() to return null (non-existing user)
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Perform the test
        User result = userService.findUserByEmail(testEmail);

        // Verify that userRepository.findByEmail() is called
        verify(userRepository, times(1)).findByEmail(testEmail);

        // Assert the result
        assertNull(result);
    }

    // Additional test cases can be added to cover more scenarios
}

