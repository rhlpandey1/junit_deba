package com.module.driveronboardingservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.module.driveronboardingservice.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AmazonS3ServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private AmazonS3ServiceImpl amazonS3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpload() {
        String path = "test-bucket";
        String fileName = "test-file.txt";
        long fileSize = 1024; // Size in bytes

        MultipartFile mockMultipartFile = new MockMultipartFile(
                fileName,
                "Test content".getBytes()
        );

        // Perform the test
        amazonS3Service.upload(path, fileName, Optional.empty(), mockMultipartFile);

        // Verify that amazonS3.putObject() is called with the correct parameters
        verify(amazonS3, times(1))
                .putObject(eq(path), eq(fileName), any(), any(ObjectMetadata.class));
    }

    @Test
    void testUpload_WithMetaData() {
        String path = "test-bucket";
        String fileName = "test-file.txt";
        long fileSize = 1024; // Size in bytes

        Map<String, String> metaData = new HashMap<>();
        metaData.put("Key1", "Value1");
        metaData.put("Key2", "Value2");

        MultipartFile mockMultipartFile = new MockMultipartFile(
                fileName,
                "Test content".getBytes()
        );

        // Perform the test
        amazonS3Service.upload(path, fileName, Optional.of(metaData), mockMultipartFile);

        // Verify that amazonS3.putObject() is called with the correct parameters
        verify(amazonS3, times(1))
                .putObject(eq(path), eq(fileName), any(), any(ObjectMetadata.class));
    }

    @Test
    void testDownload() throws IOException {
        String path = "test-bucket";
        String key = "test-file.txt";

        // Mock the behavior of amazonS3.getObject()
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockObjectContent = new S3ObjectInputStream(
                new ByteArrayInputStream("Test content".getBytes()),
                null
        );
        when(mockS3Object.getObjectContent()).thenReturn(mockObjectContent);
        when(amazonS3.getObject(path, key)).thenReturn(mockS3Object);

        // Perform the test
        byte[] result = amazonS3Service.download(path, key);

        // Verify that amazonS3.getObject() is called with the correct parameters
        verify(amazonS3, times(1)).getObject(path, key);

        // Assert that the downloaded content is correct
        assertArrayEquals("Test content".getBytes(), result);
    }

}

