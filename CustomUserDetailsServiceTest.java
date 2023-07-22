package com.module.driveronboardingservice.service;

import com.module.driveronboardingservice.dao.Role;
import com.module.driveronboardingservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_ValidUser() {
        String username = "test@example.com";
        String password = "testPassword";

        // Create a mock User object
        User mockUser = new User(username, password, new ArrayList<>());

        // Mock the behavior of userRepository.findByEmail()
        //when(userRepository.findByEmail(username)).thenReturn(mockUser);

        // Call the method to be tested
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Verify that userRepository.findByEmail() is called
        verify(userRepository, times(1)).findByEmail(username);

        // Assert that the returned UserDetails object is not null
        assertNotNull(userDetails);

        // Assert that the returned UserDetails object has the correct username and password
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_InvalidUser() {
        String username = "nonexistent@example.com";

        // Mock the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(username)).thenReturn(null);

        // Call the method to be tested and expect an exception
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        // Verify that userRepository.findByEmail() is called
        verify(userRepository, times(1)).findByEmail(username);
    }

    @Test
    void testMapRolesToAuthorities() {
        // Create a list of Role objects
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("ROLE_USER"));
        roles.add(new Role("ROLE_ADMIN"));

        // Call the method to be tested
        Collection<? extends GrantedAuthority> authorities = customUserDetailsService.mapRolesToAuthorities(roles);

        // Assert that the authorities collection is not empty
        assertNotNull(authorities);

        // Assert that the authorities collection contains the correct number of roles
        assertEquals(roles.size(), authorities.size());

        // Assert that each authority in the collection has the correct role name
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        for (GrantedAuthority authority : authorities) {
            assertTrue(roleNames.contains(authority.getAuthority()));
        }
    }

    // Additional test cases can be added to cover more scenarios
}
