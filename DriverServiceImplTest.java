package com.module.driveronboardingservice.service;

import com.module.driveronboardingservice.constants.Status;
import com.module.driveronboardingservice.dao.Address;
import com.module.driveronboardingservice.dao.Driver;
import com.module.driveronboardingservice.dao.File;
import com.module.driveronboardingservice.dao.ShippingDetails;
import com.module.driveronboardingservice.dto.FileDto;
import com.module.driveronboardingservice.dto.ShippingDto;
import com.module.driveronboardingservice.dto.UserRegistration;
import com.module.driveronboardingservice.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.module.driveronboardingservice.repository.*;

class DriverServiceImplTest {

    private static final String TEST_DRIVER_EMAIL = "test@example.com";
    private static final String TEST_DOCUMENT_TYPE = "TestDocument";
    private static final String TEST_FILE_NAME = "test-file.txt";
    private static final String TEST_SHIPPING_EMAIL = "shipping@example.com";
    private static final String TEST_SHIPPING_TRACKING_ID = "123456";

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ShippingRepository shippingRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private DriverServiceImpl driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDriverDetails() {
        // Mock the behavior of addressRepository.save()
        Address mockAddress = new Address();
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

        // Mock the behavior of driverRepository.save()
        Driver mockDriver = new Driver();
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);

        // Perform the test
        UserRegistration testRegistrationDto = new UserRegistration();
        testRegistrationDto.setEmail(TEST_DRIVER_EMAIL);
        testRegistrationDto.setFirstName("John");
        testRegistrationDto.setLastName("Doe");
        testRegistrationDto.setCity("City");
        testRegistrationDto.setAddress("Address");
        testRegistrationDto.setPinCode(123456);
        testRegistrationDto.setPhoneNumber("1234567890");
        driverService.saveDriverDetails(testRegistrationDto);

        // Verify that addressRepository.save() is called
        verify(addressRepository, times(1)).save(any(Address.class));

        // Verify that driverRepository.save() is called
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testMarkRideAvailability_ValidStatus() {
        // Mock the behavior of driverRepository.findByEmail()
        Driver mockDriver = new Driver();
        mockDriver.setAvailableForRide("NOT_AVAILABLE");
        when(driverRepository.findByEmail(TEST_DRIVER_EMAIL)).thenReturn(mockDriver);

        // Perform the test
        Map<String, String> testRequestMap = new HashMap<>();
        testRequestMap.put("availableForRide", "AVAILABLE");
        String result = driverService.markRideAvailability(testRequestMap, TEST_DRIVER_EMAIL);

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_DRIVER_EMAIL);

        // Verify that driverRepository.save() is called
        verify(driverRepository, times(1)).save(any(Driver.class));

        // Assert the result message
        assertEquals("Driver is AVAILABLE for ride", result);
    }

    @Test
    void testMarkRideAvailability_InvalidStatus() {
        // Mock the behavior of driverRepository.findByEmail()
        Driver mockDriver = new Driver();
        mockDriver.setAvailableForRide("NOT_AVAILABLE");
        when(driverRepository.findByEmail(TEST_DRIVER_EMAIL)).thenReturn(mockDriver);

        // Perform the test
        Map<String, String> testRequestMap = new HashMap<>();
        testRequestMap.put("availableForRide", "INVALID_STATUS");

        // Call the method to be tested and expect an exception
        assertThrows(InvalidRequestException.class, () -> {
            driverService.markRideAvailability(testRequestMap, TEST_DRIVER_EMAIL);
        });

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_DRIVER_EMAIL);
    }

    @Test
    void testGetRejectedDocuments() {
        // Mock the behavior of driverRepository.findByEmail()
        Driver mockDriver = new Driver();
        List<File> mockFiles = new ArrayList<>();
        mockFiles.add(new File(TEST_FILE_NAME, TEST_DOCUMENT_TYPE, Status.REJECTED.name()));
        mockFiles.add(new File("other-file.txt", "OtherDocument", Status.EXPIRED.name()));
        mockDriver.setFiles(mockFiles);
        when(driverRepository.findByEmail(TEST_DRIVER_EMAIL)).thenReturn(mockDriver);

        // Perform the test
        List<FileDto> result = driverService.getRejectedDocuments(TEST_DRIVER_EMAIL);

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_DRIVER_EMAIL);

        // Assert the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TEST_FILE_NAME, result.get(0).getFileName());
        assertEquals(TEST_DOCUMENT_TYPE, result.get(0).getDocumentType());
        assertEquals(Status.REJECTED.name(), result.get(0).getStatus());
    }

    @Test
    void testUpdateShippingDetails_NewTrackingId() throws Exception {
        // Mock the behavior of driverRepository.findByEmail()
        Driver mockDriver = new Driver();
        when(driverRepository.findByEmail(TEST_SHIPPING_EMAIL)).thenReturn(mockDriver);

        // Perform the test
        ShippingDto testShippingDto = new ShippingDto();
        testShippingDto.setUserEmail(TEST_SHIPPING_EMAIL);
        testShippingDto.setCourierCompany("TestCourier");
        testShippingDto.setDeviceId("1234567890");
        testShippingDto.setTrackingId(TEST_SHIPPING_TRACKING_ID);
        testShippingDto.setDeliveryStatus("PENDING");
        testShippingDto.setEstimatedDate(new Date("2023-07-01"));
        driverService.updateShippingDetails(testShippingDto);

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_SHIPPING_EMAIL);

        // Verify that shippingRepository.save() is called
        verify(shippingRepository, times(1)).save(any(ShippingDetails.class));


    }
}

