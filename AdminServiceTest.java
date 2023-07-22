package com.module.driveronboardingservice.service;

import com.module.driveronboardingservice.dao.Driver;
import com.module.driveronboardingservice.dao.File;
import com.module.driveronboardingservice.dao.RequiredDocuments;
import com.module.driveronboardingservice.dto.FileDto;
import com.module.driveronboardingservice.repository.DriverRepository;
import com.module.driveronboardingservice.repository.FileRepository;
import com.module.driveronboardingservice.repository.RequiredDocumentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private AdminService adminService;
    @Mock
    private RequiredDocumentsRepository documentsRepository;
    @Mock
    private DriverRepository driverRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateFileStatusByAdmin() {
        List<FileDto> fileDtoList = new ArrayList<>();
        // Add fileDto objects to the list here (create FileDto objects with appropriate data)

        // Mock the behavior of fileRepository.findById()
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(new File()));

        // Call the method to be tested
        adminService.updateFileStatusByAdmin(fileDtoList);

        // Verify that fileRepository.save() is called for each FileDto in the list
        verify(fileRepository, times(fileDtoList.size())).save(any(File.class));
    }
    @Test
    void testSaveRequiredDocuments() {
        List<String> documentList = new ArrayList<>();
        // Add documents to the list here

        // Mock the behavior of documentsRepository.save()
        when(documentsRepository.save(any(RequiredDocuments.class))).thenReturn(new RequiredDocuments());

        // Call the method to be tested
        adminService.saveRequiredDocuments(documentList, "ROLE_ADMIN");

        // Verify that documentsRepository.save() is called
        verify(documentsRepository, times(1)).save(any(RequiredDocuments.class));
    }


    @Test
    void testApproveAllFiles() {
        String email = "test@example.com";

        // Mock the behavior of driverRepository.findByEmail()
        Driver driver = new Driver();
        driver.setEmail(email);
        List<File> files = new ArrayList<>();
        // Add File objects to the files list here (create File objects with appropriate data)
        driver.setFiles(files);
        when(driverRepository.findByEmail(email)).thenReturn(driver);

        // Call the method to be tested
        adminService.approveAllFiles(email);

        // Verify that fileRepository.save() is called for each File in the driver's files list
        verify(fileRepository, times(files.size())).save(any(File.class));

        // Verify that driverRepository.save() is called once for the driver
        verify(driverRepository, times(1)).save(any(Driver.class));
    }


}