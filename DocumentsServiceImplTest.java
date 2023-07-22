package com.module.driveronboardingservice.service;

import com.module.driveronboardingservice.constants.Status;
import com.module.driveronboardingservice.dao.Driver;
import com.module.driveronboardingservice.dao.File;
import com.module.driveronboardingservice.dao.RequiredDocuments;
import com.module.driveronboardingservice.repository.DriverRepository;
import com.module.driveronboardingservice.repository.FileRepository;
import com.module.driveronboardingservice.repository.RequiredDocumentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DocumentsServiceImplTest {

    private static final String TEST_BUCKET_NAME = "test-bucket";
    private static final String TEST_DRIVER_EMAIL = "test@example.com";
    private static final String TEST_DOCUMENT_TYPE = "TestDocument";
    private static final String TEST_FILE_NAME = "test-file.txt";

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RequiredDocumentsRepository documentsRepository;

    @InjectMocks
    private DocumentsServiceImpl documentsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile_ValidDriver_NewFile() throws Exception {
        // Mock the behavior of driverRepository.findByEmail()
        Driver mockDriver = new Driver();
        when(driverRepository.findByEmail(TEST_DRIVER_EMAIL)).thenReturn(mockDriver);
        when(fileRepository.save(any(File.class))).thenReturn(new File());

        // Perform the test
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                TEST_FILE_NAME,
                "Test content".getBytes()
        );
        documentsService.uploadFile(mockMultipartFile, TEST_DRIVER_EMAIL, TEST_DOCUMENT_TYPE);

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_DRIVER_EMAIL);

        // Verify that fileRepository.save() is called
        verify(fileRepository, times(1)).save(any(File.class));

        // Verify that amazonS3Service.upload() is called
        verify(amazonS3Service, times(1))
                .upload(anyString(), anyString(), any(), any(MockMultipartFile.class));
    }

    @Test
    void testUploadFile_ValidDriver_ExistingFile() throws Exception {
        // Mock the behavior of driverRepository.findByEmail()
        Driver mockDriver = new Driver();
        List<File> existingFiles = new ArrayList<>();
        existingFiles.add(new File(TEST_FILE_NAME, TEST_DOCUMENT_TYPE, Status.IN_REVIEW.toString()));
        mockDriver.setFiles(existingFiles);
        when(driverRepository.findByEmail(TEST_DRIVER_EMAIL)).thenReturn(mockDriver);

        // Perform the test
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                TEST_FILE_NAME,
                "Test content".getBytes()
        );

        // Call the method to be tested and expect an exception
        assertThrows(FileAlreadyExistsException.class, () -> {
            documentsService.uploadFile(mockMultipartFile, TEST_DRIVER_EMAIL, TEST_DOCUMENT_TYPE);
        });

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_DRIVER_EMAIL);
    }

    @Test
    void testUploadFile_InvalidDriver() throws Exception {
        // Mock the behavior of driverRepository.findByEmail()
        when(driverRepository.findByEmail(TEST_DRIVER_EMAIL)).thenReturn(null);

        // Perform the test
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                TEST_FILE_NAME,
                "Test content".getBytes()
        );

        // Call the method to be tested and expect an exception
        assertThrows(UsernameNotFoundException.class, () -> {
            documentsService.uploadFile(mockMultipartFile, TEST_DRIVER_EMAIL, TEST_DOCUMENT_TYPE);
        });

        // Verify that driverRepository.findByEmail() is called
        verify(driverRepository, times(1)).findByEmail(TEST_DRIVER_EMAIL);
    }

    @Test
    void testDownload() {
        // TODO: Implement test for the download method (requires S3 mock or testing AWS SDK)
    }

    @Test
    void testGetRequiredDocuments() throws Exception {
        String testRole = "DRIVER";
        List<String> testDocuments = new ArrayList<>();
        testDocuments.add("Document1");
        testDocuments.add("Document2");

        // Mock the behavior of documentsRepository.findByRole()
        RequiredDocuments mockRequiredDocuments = new RequiredDocuments();
        mockRequiredDocuments.setRole(testRole);
        mockRequiredDocuments.setDocuments(testDocuments.toString());
        when(documentsRepository.findByRole(testRole)).thenReturn(mockRequiredDocuments);

        // Perform the test
        List<String> result = documentsService.getRequiredDocuments(testRole);

        // Verify that documentsRepository.findByRole() is called
        verify(documentsRepository, times(1)).findByRole(testRole);

        // Assert that the result is as expected
        assertNotNull(result);
        assertEquals(testDocuments.size(), result.size());
        assertEquals(testDocuments.get(0), result.get(0));
        assertEquals(testDocuments.get(1), result.get(1));
    }

    @Test
    void testUpdateDocument() throws Exception {
        long testFileId = 1L;

        // Mock the behavior of fileRepository.findById()
        File mockFile = new File();
        when(fileRepository.findById(testFileId)).thenReturn(Optional.of(mockFile));

        // Perform the test
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                TEST_FILE_NAME,
                "Test content".getBytes()
        );
        documentsService.updateDocument(testFileId, mockMultipartFile);

        // Verify that fileRepository.findById() is called
        verify(fileRepository, times(1)).findById(testFileId);

        // Verify that amazonS3Service.upload() is called
        verify(amazonS3Service, times(1))
                .upload(anyString(), anyString(), any(), any(MockMultipartFile.class));

        // Verify that fileRepository.save() is called
        verify(fileRepository, times(1)).save(any(File.class));
    }

    // Additional test cases can be added to cover more scenarios
}
