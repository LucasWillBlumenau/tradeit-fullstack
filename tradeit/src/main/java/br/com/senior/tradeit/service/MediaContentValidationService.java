package br.com.senior.tradeit.service;

import br.com.senior.tradeit.infra.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
public class MediaContentValidationService {

    private final MediaManagementService mediaManagementService;

    public MediaContentValidationService(MediaManagementService mediaManagementService) {
        this.mediaManagementService = mediaManagementService;
    }

    public void validate(MultipartFile[] images, MultipartFile video) {
        images = filterNotEmptyFiles(images);
        if (images.length == 0) {
            throw new BadRequestException("o cadastro deve ter, pelo menos, uma imagem do item");
        }
        if (images.length > 5) {
            throw new BadRequestException("o cadastro pode conter, no máximo, 5 imagens");
        }
        if (video.getSize() == 0) {
            throw new BadRequestException("o cadastro deve ter um vídeo");
        }

        for (MultipartFile image : images) {
            validateImageContentType(image.getContentType());
        }
        validateVideoContentType(video.getContentType());
    }

    private MultipartFile[] filterNotEmptyFiles(MultipartFile[] files) {
        int filesWithContentCount = Math.toIntExact(Arrays.stream(files)
                .filter(file -> file.getSize() > 0)
                .count());

        MultipartFile[] filteredFiles = new MultipartFile[filesWithContentCount];
        int currentIndex = 0;
        for (MultipartFile file : files) {
            if (file.getSize() > 0) {
                filteredFiles[currentIndex++] = file;
            }
        }
        return filteredFiles;
    }

    private void validateVideoContentType(String contentType) {
        if (!mediaManagementService.isValidVideoContentType(contentType)) {
            throw new BadRequestException("o tipo de vídeo " + contentType + " não é suportado");
        }
    }

    private void validateImageContentType(String contentType) {
        if (!mediaManagementService.isValidImageContentType(contentType)) {
            throw new BadRequestException("o tipo de imagem " + contentType + " não é suportado");
        }
    }
}
