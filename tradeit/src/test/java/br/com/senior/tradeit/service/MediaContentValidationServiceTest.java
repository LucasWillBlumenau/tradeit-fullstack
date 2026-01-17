package br.com.senior.tradeit.service;

import br.com.senior.tradeit.infra.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MediaContentValidationServiceTest {

    private MediaContentValidationService mediaContentValidationService;

    @BeforeEach
    void setUp() {
        MediaManagementService mediaManagementService = new MediaManagementService(null);
        mediaContentValidationService = new MediaContentValidationService(mediaManagementService);
    }

    @ParameterizedTest
    @MethodSource
    void validate_shouldDoNothing_whenImagesAndVideoSetIsValid(MultipartFile[] images, MultipartFile video) {
        mediaContentValidationService.validate(images, video);
    }

    static Stream<Arguments> validate_shouldDoNothing_whenImagesAndVideoSetIsValid() {
        return Stream.of(
                Arguments.of(
                        new MultipartFile[] {
                                mockFile("image/png", 50),
                                mockFile("image/jpeg", 400),
                                mockFile("image/jpeg", 300),
                                mockFile("image/png", 250),
                                mockFile("image/png", 100),
                        },
                        mockFile("video/mp4", 1_000)
                ),
                Arguments.of(
                        new MultipartFile[] {
                                mockFile("image/png", 5_000),
                                mockFile("image/jpeg", 1_000),
                                mockFile("image/jpeg", 1_000),
                                mockFile("image/png", 1_000),
                                mockFile("image/png", 5_000),
                                mockFile("image/png", 0),
                                mockFile("image/jpeg", 0),
                        },
                        mockFile("video/mp4", 1_000_000)
                ),
                Arguments.of(
                        new MultipartFile[] { mockFile("image/png", 50_000) },
                        mockFile("video/mp4", 1_000_000)
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void validate_shouldThrowBadRequestException_whenThereAreNoImages(MultipartFile[] images) {
        MultipartFile video = mockFile("video/mp4", 1_000_000);
        var exc = assertThrows(BadRequestException.class, () -> mediaContentValidationService.validate(images, video));
        assertEquals("o cadastro deve ter, pelo menos, uma imagem do item", exc.getMessage());
    }

    static Stream<Arguments> validate_shouldThrowBadRequestException_whenThereAreNoImages() {
        return Stream.of(
            Arguments.of((Object) new MultipartFile[0]),
            Arguments.of((Object) new MultipartFile[] {
                    mockFile("image/jpg", 0),
                    mockFile("image/jpg", 0),
                    mockFile("image/jpg", 0),
            })
        );
    }

    @Test
    void validate_shouldThrowBadRequestException_whenThereAreMoreThanFiveImages() {
        MultipartFile[] images = {
                mockFile("image/png", 10),
                mockFile("image/webp", 5),
                mockFile("image/jpeg", 25),
                mockFile("image/jpeg", 250),
                mockFile("image/png", 100),
                mockFile("image/webp", 100),
        };
        MultipartFile video = mockFile("video/mp4", 10_000);

        var exc = assertThrows(BadRequestException.class, () -> mediaContentValidationService.validate(images, video));
        assertEquals("o cadastro pode conter, no máximo, 5 imagens", exc.getMessage());
    }

    @Test
    void validate_shouldThrowBadRequestException_whenThereAreIsNoVideo() {
        MultipartFile[] images = { mockFile("image/jpeg", 10_000) };
        MultipartFile video = mockFile("video/mp4", 0);

        var exc = assertThrows(BadRequestException.class, () -> mediaContentValidationService.validate(images, video));
        assertEquals("o cadastro deve ter um vídeo", exc.getMessage());
    }

    @Test
    void validate_shouldThrowBadRequestException_whenImageFormatIsNotSupported() {
        MultipartFile[] images = { mockFile("image/bmp", 10_000) };
        MultipartFile video = mockFile("video/mp4", 100);

        var exc = assertThrows(BadRequestException.class, () -> mediaContentValidationService.validate(images, video));
        assertEquals("o tipo de imagem image/bmp não é suportado", exc.getMessage());
    }

    @Test
    void validate_shouldThrowBadRequestException_whenVideoFormatIsNotSupported() {
        MultipartFile[] images = { mockFile("image/png", 10_000) };
        MultipartFile video = mockFile("video/mkv", 100);

        var exc = assertThrows(BadRequestException.class, () -> mediaContentValidationService.validate(images, video));
        assertEquals("o tipo de vídeo video/mkv não é suportado", exc.getMessage());
    }


    static MultipartFile mockFile(String contentType, int size) {
        return new MockMultipartFile("file", "", contentType, new byte[size]);
    }
}