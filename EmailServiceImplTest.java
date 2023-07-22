package com.module.driveronboardingservice.service;

import com.amazonaws.services.chime.model.UpdateSipMediaApplicationCallRequest;
import com.module.driveronboardingservice.dao.Driver;
import com.module.driveronboardingservice.dto.ShippingDto;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendShippingDetailsMail() throws Exception {
        // Prepare input data
        Driver mockDriver = new Driver();
        mockDriver.setFirstName("John");
        mockDriver.setLastName("Doe");

        ShippingDto shippingDto = new ShippingDto();
        shippingDto.setUserEmail("test@example.com");
        shippingDto.setCourierCompany("TestCourier");
        shippingDto.setTrackingId("123456");
        shippingDto.setEstimatedDate(new Date("2023-07-31"));
        shippingDto.setDeliveryStatus("Delivered");

        // Mock the behavior of javaMailSender.createMimeMessage()
        MimeMessage mockMimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // Mock the behavior of MimeMessageHelper
        MimeMessageHelper mockMimeMessageHelper = mock(MimeMessageHelper.class);

        // Perform the test
        assertDoesNotThrow(() -> emailService.sendShippingDetailsMail(mockDriver, shippingDto));

        // Verify that javaMailSender.createMimeMessage() is called
        verify(javaMailSender, times(1)).createMimeMessage();

        // Verify that MimeMessageHelper methods are called
        verify(mockMimeMessageHelper, times(1)).setFrom(anyString());
        verify(mockMimeMessageHelper, times(1)).setTo(anyString());
        verify(mockMimeMessageHelper, times(1)).setText(anyString(), anyBoolean());
        verify(mockMimeMessageHelper, times(1)).setSubject(anyString());
        verify(mockMimeMessageHelper, times(1)).setFrom(anyString());

        // Verify that javaMailSender.send() is called
        verify(javaMailSender, times(1)).send(mockMimeMessage);

        // Verify that the email service does not throw any exception
    }


    @Test
    void testSendShippingDetailsMail_MessagingException() throws Exception {
        // Prepare input data (Empty driver and shippingDto)
        Driver mockDriver = new Driver();
        ShippingDto shippingDto = new ShippingDto();

        // Mock the behavior of javaMailSender.createMimeMessage()
        MimeMessage mockMimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // Mock the behavior of MimeMessageHelper
        MimeMessageHelper mockMimeMessageHelper = mock(MimeMessageHelper.class);

        // Mock the behavior of javaMailSender.send() to throw MessagingException
        doThrow(MessagingException.class).when(javaMailSender).send(any(MimeMessage.class));

        // Perform the test and expect a MessagingException
        assertThrows(MessagingException.class, () -> emailService.sendShippingDetailsMail(mockDriver, shippingDto));

        // Verify that javaMailSender.createMimeMessage() is called
        verify(javaMailSender, times(1)).createMimeMessage();

        // Verify that MimeMessageHelper methods are called
        verify(mockMimeMessageHelper, times(1)).setFrom(anyString());
        verify(mockMimeMessageHelper, times(1)).setTo(anyString());
        verify(mockMimeMessageHelper, times(1)).setText(anyString(), anyBoolean());
        verify(mockMimeMessageHelper, times(1)).setSubject(anyString());
        verify(mockMimeMessageHelper, times(1)).setFrom(anyString());

        // Verify that javaMailSender.send() is called
        verify(javaMailSender, times(1)).send(mockMimeMessage);
    }

    // Additional test cases can be added to cover more scenarios
}
