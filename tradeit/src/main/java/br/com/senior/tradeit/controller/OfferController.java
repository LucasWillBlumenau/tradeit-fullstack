package br.com.senior.tradeit.controller;

import br.com.senior.tradeit.dto.offer.ContactDTO;
import br.com.senior.tradeit.dto.offer.OfferCreationDTO;
import br.com.senior.tradeit.dto.offer.OfferDetailsDTO;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OfferDetailsDTO> createOffer(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid OfferCreationDTO offerCreationDTO
    ) {
        OfferDetailsDTO offerDetailsDTO = offerService.createOffer(user, offerCreationDTO);
        return ResponseEntity.ok(offerDetailsDTO);
    }

    @GetMapping
    public ResponseEntity<List<OfferDetailsDTO>> getOffersFromAdvertisement(
            @AuthenticationPrincipal User user,
            @RequestParam Long advertisementId
    ) {
        var offersDetails = offerService.getOffersFromAdvertisement(user, advertisementId);
        return ResponseEntity.ok(offersDetails);
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<OfferDetailsDTO>> getUserOffers(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        Page<OfferDetailsDTO> offerDetailsDTO = offerService.getOffersMadeByUser(user, pageable);
        return ResponseEntity.ok()
                .body(offerDetailsDTO);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOffer(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        offerService.cancelOffer(user, id);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptOffer(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid ContactDTO contact
            ) {
        offerService.acceptOffer(user, id, contact);
        return ResponseEntity.noContent()
                .build();
    }

}
