package br.com.senior.tradeit.controller;

import br.com.senior.tradeit.dto.media.MediaDTO;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.service.AdvertisementService;
import br.com.senior.tradeit.service.OfferService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("resources")
public class ResourceController {

    private final AdvertisementService advertisementService;
    private final OfferService offerService;

    public ResourceController(AdvertisementService advertisementService, OfferService offerService) {
        this.advertisementService = advertisementService;
        this.offerService = offerService;
    }

    @GetMapping("/advertisements/{id}/images/{displayOrder}")
    public ResponseEntity<Resource> getAdvertisementImage(
            @PathVariable Long id,
            @PathVariable Long displayOrder
    ) {
        MediaDTO media = advertisementService.getAdvertisementImage(id, displayOrder);
        return ResponseEntity.ok()
                .contentType(media.mediaType())
                .body(media.resource());
    }

    @GetMapping("/advertisements/{id}/video")
    public ResponseEntity<Resource> getAdvertisementVideo(@PathVariable Long id) {
        MediaDTO media = advertisementService.getAdvertisementVideo(id);
        return ResponseEntity.ok()
                .contentType(media.mediaType())
                .body(media.resource());
    }

    @GetMapping("/offers/images/{id}")
    public ResponseEntity<Resource> getOfferImage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        MediaDTO media = offerService.getImage(user, id);
        return ResponseEntity.ok()
                .contentType(media.mediaType())
                .body(media.resource());
    }

    @GetMapping("/offers/{id}/video")
    public ResponseEntity<Resource> getOfferVideo(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        MediaDTO media = offerService.getVideo(user, id);
        return ResponseEntity.ok()
                .contentType(media.mediaType())
                .body(media.resource());
    }
}
