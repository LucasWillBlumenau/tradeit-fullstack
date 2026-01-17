package br.com.senior.tradeit.service;

import br.com.senior.tradeit.infra.exception.ServerErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;

class MediaManagementServiceTest {

    private MediaManagementService mediaManagementService;

    @BeforeEach
    void setUp() {
        mediaManagementService = new MediaManagementService(".uploads");
    }

    @ParameterizedTest
    @ValueSource(strings = {"video/mp4"})
    void isValidVideoContentType_shouldReturnTrue_whenContentTypeIsValid(String contentType) {
        assertTrue(mediaManagementService.isValidVideoContentType(contentType));
    }

    @ParameterizedTest
    @ValueSource(strings = {"video/mkv", "video/webm", "video/flv", "image/png", "image/jpeg", "image/bmp"})
    void isValidVideoContentType_shouldReturnFalse_whenContentTypeIsNotValid(String contentType) {
        assertFalse(mediaManagementService.isValidVideoContentType(contentType));
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/png", "image/jpeg", "image/webp"})
    void isValidImageContentType_shouldReturnTrue_whenContentTypeIsValid(String contentType) {
        assertTrue(mediaManagementService.isValidImageContentType(contentType));
    }

    @ParameterizedTest
    @ValueSource(strings = {"video/mkv", "video/webm", "video/flv", "image/gif", "image/bmp"})
    void isValidImageContentType_shouldReturnFalse_whenContentTypeIsNotValid(String contentType) {
        assertFalse(mediaManagementService.isValidImageContentType(contentType));
    }

    @ParameterizedTest
    @CsvSource({
            "'image/png', 'image', 'png'",
            "'image/jpeg', 'image', 'jpeg'",
            "'image/webp', 'image', 'webp'"
    })
    void resolveImageHttpMediaType_shouldReturnMediaType_whenNameIsValid(String mediaTypeName, String type, String subType) {
        MediaType mediaType = mediaManagementService.resolveImageHttpMediaType(mediaTypeName);
        assertEquals(type, mediaType.getType());
        assertEquals(subType, mediaType.getSubtype());
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/bmp", "image/flv", "video/mp4"})
    void resolveImageHttpMediaType_shouldThrowServerErrorException_whenNameIsInvalid(String mediaTypeName) {
        assertThrows(ServerErrorException.class, () -> mediaManagementService.resolveImageHttpMediaType(mediaTypeName));
    }

    @ParameterizedTest
    @CsvSource({ "'video/mp4', 'application', 'octet-stream'" })
    void resolveVideoHttpMediaType_shouldReturnMediaType_whenNameIsValid(String mediaTypeName, String type, String subType) {
        MediaType mediaType = mediaManagementService.resolveVideoHttpMediaType(mediaTypeName);
        assertEquals(type, mediaType.getType());
        assertEquals(subType, mediaType.getSubtype());
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/bmp", "image/flv", "video/mkv"})
    void resolveVideoHttpMediaType_shouldThrowServerErrorException_whenNameIsInvalid(String mediaTypeName) {
        assertThrows(ServerErrorException.class, () -> mediaManagementService.resolveVideoHttpMediaType(mediaTypeName));
    }


}