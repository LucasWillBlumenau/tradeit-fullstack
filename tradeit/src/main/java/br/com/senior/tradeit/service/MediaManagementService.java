package br.com.senior.tradeit.service;

import br.com.senior.tradeit.infra.exception.ServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MediaManagementService {

    private static final Map<String, MediaType> SUPPORTED_VIDEO_FILE_FORMATS = Map.of(
            "video/mp4", MediaType.APPLICATION_OCTET_STREAM
    );
    private static final Map<String, MediaType> SUPPORTED_IMAGE_FILE_FORMATS = Map.of(
            "image/png", MediaType.IMAGE_PNG,
            "image/jpeg", MediaType.IMAGE_JPEG,
            "image/webp", MediaType.valueOf("image/webp")
    );


    private final String uploadsPath;

    public MediaManagementService(@Value("${file.uploads_location}") String uploadsPath) {
        this.uploadsPath = uploadsPath;
    }

    public boolean isValidVideoContentType(String contentType) {
        return SUPPORTED_VIDEO_FILE_FORMATS.containsKey(contentType);
    }

    public boolean isValidImageContentType(String contentType) {
        return SUPPORTED_IMAGE_FILE_FORMATS.containsKey(contentType);
    }

    public UrlResource resolvePath(String directory, UUID fileId) {
        File file = Paths.get(uploadsPath, directory, fileId.toString())
                .toFile();
        try {
            var resource = new UrlResource(file.toURI());
            if (!resource.exists()) {
                throw new ServerErrorException();
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ServerErrorException();
        }
    }

    public MediaType resolveImageHttpMediaType(String contentType) {
        MediaType mediaType = SUPPORTED_IMAGE_FILE_FORMATS.get(contentType);
        return getMediaTypeOrThrowError(mediaType);
    }

    public MediaType resolveVideoHttpMediaType(String contentType) {
        MediaType mediaType = SUPPORTED_VIDEO_FILE_FORMATS.get(contentType);
        return getMediaTypeOrThrowError(mediaType);
    }

    private static MediaType getMediaTypeOrThrowError(MediaType mediaType) {
        if (mediaType == null) {
            throw new ServerErrorException();
        }
        return mediaType;
    }

    public List<UUID> saveFiles(String destinationDirectory, MultipartFile[] files) {
        List<UUID> fileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            UUID fileId = saveFile(destinationDirectory, file);
            fileIds.add(fileId);
        }
        return fileIds;
    }

    public UUID saveFile(String destinationDirectory, MultipartFile file) {
        Path location = Paths.get(uploadsPath, destinationDirectory);
        boolean __ = location.toFile().mkdirs();

        File destination = null;
        UUID fileId = null;
        boolean filePathNotChosen = true;
        while (filePathNotChosen) {
            fileId = UUID.randomUUID();
            destination = Paths.get(uploadsPath, destinationDirectory, fileId.toString())
                    .toFile()
                    .getAbsoluteFile();
            filePathNotChosen = destination.exists();
        }

        try {
            if (!destination.createNewFile()) {
                throw new IOException();
            }
            file.transferTo(destination);
        } catch (IOException e) {
            throw new ServerErrorException();
        }
        return fileId;
    }

}
