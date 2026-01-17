package br.com.senior.tradeit.controller;

import br.com.senior.tradeit.dto.advertisement.AdvertisementCreationDTO;
import br.com.senior.tradeit.dto.advertisement.AdvertisementDetailsDTO;
import br.com.senior.tradeit.dto.advertisement.AdvertisementSummaryDTO;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementDetailsDTO> createAdvertisement(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid AdvertisementCreationDTO advertisementCreationDTO,
            UriComponentsBuilder uriBuilder
    ) {
        advertisementService.createAdvertisement(user, advertisementCreationDTO);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDetailsDTO> getAdvertisement(@PathVariable Long id) {
        var advertisementDetails = advertisementService.getAdvertisement(id);
        return ResponseEntity.ok(advertisementDetails);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<AdvertisementSummaryDTO>> getUserAdvertisements(@AuthenticationPrincipal User user) {
        var advertisementsDetails = advertisementService.getUserAdvertisements(user);
        return ResponseEntity.ok(advertisementsDetails);
    }

    @GetMapping
    public ResponseEntity<Page<AdvertisementSummaryDTO>> searchAdvertisement(Pageable pageable) {
        var advertisementsPage = advertisementService.getAdvertisements(pageable);
        return ResponseEntity.ok(advertisementsPage);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAdvertisement(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        advertisementService.cancelAdvertisement(user, id);
        return ResponseEntity.noContent()
                .build();
    }
}
