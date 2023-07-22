package com.module.driveronboardingservice.service;

import com.amazonaws.services.lambda.model.UnsupportedMediaTypeException;
import com.module.driveronboardingservice.exception.DocumentTypeMisMatchException;
import com.module.driveronboardingservice.exception.EmptyFileException;
import com.module.driveronboardingservice.validator.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FileValidatorTest {
    private static final String VALID_DOCUMENT_TYPE = "VALID_DOCUMENT_TYPE";
    private static final String INVALID_DOCUMENT_TYPE = "INVALID_DOCUMENT_TYPE";

    @Mock
    private DocumentsService documentsService;

    @InjectMocks
    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValidFile_WithValidDocumentType() throws Exception {
        // Mock the behavior of documentsService.getRequiredDocuments()
        List<String> requiredDocs = new ArrayList<>();
        requiredDocs.add(VALID_DOCUMENT_TYPE);
        when(documentsService.getRequiredDocuments(anyString())).thenReturn(requiredDocs);

        // Perform the test
        MultipartFile mockMultipartFile = new MockMultipartFile("testFile.pdf", new byte[]{});
        fileValidator.isValidFile(mockMultipartFile, VALID_DOCUMENT_TYPE);

        // No exception should be thrown if the document type is valid
    }

    @Test
    void testIsValidFile_WithInvalidDocumentType() throws Exception {
        // Mock the behavior of documentsService.getRequiredDocuments()
        List<String> requiredDocs = new ArrayList<>();
        requiredDocs.add(VALID_DOCUMENT_TYPE);
        when(documentsService.getRequiredDocuments(anyString())).thenReturn(requiredDocs);

        // Perform the test
        MultipartFile mockMultipartFile = new MockMultipartFile("testFile.pdf", new byte[]{});

        // An exception of type DocumentTypeMisMatchException should be thrown
        assertThrows(DocumentTypeMisMatchException.class, () -> {
            fileValidator.isValidFile(mockMultipartFile, INVALID_DOCUMENT_TYPE);
        });
    }

    @Test
    void testIsValidFile_WithEmptyFile() throws Exception {
        // Perform the test with an empty MultipartFile
        MultipartFile emptyMultipartFile = new MockMultipartFile("emptyFile.pdf", new byte[]{});
        assertThrows(EmptyFileException.class, () -> {
            fileValidator.isValidFile(emptyMultipartFile, VALID_DOCUMENT_TYPE);
        });
    }

    @Test
    void testIsValidFile_WithUnsupportedContentType() throws Exception {
        // Perform the test with an unsupported content type
        MultipartFile unsupportedMultipartFile = new MockMultipartFile(
                "testFile.txt",
                "Test content".getBytes()
        );
        assertThrows(UnsupportedMediaTypeException.class, () -> {
            fileValidator.isValidFile(unsupportedMultipartFile, VALID_DOCUMENT_TYPE);
        });
    }
}
