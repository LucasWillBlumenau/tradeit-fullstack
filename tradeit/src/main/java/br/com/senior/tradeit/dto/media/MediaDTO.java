package br.com.senior.tradeit.dto.media;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record MediaDTO(
        Resource resource,
        MediaType mediaType
) {
}
