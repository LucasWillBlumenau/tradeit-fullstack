package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.offer.ContactDTO;
import br.com.senior.tradeit.dto.offer.OfferCreationDTO;
import br.com.senior.tradeit.dto.offer.OfferDetailsDTO;
import br.com.senior.tradeit.entity.advertisement.Advertisement;
import br.com.senior.tradeit.entity.advertisement.AdvertisementStatus;
import br.com.senior.tradeit.entity.condition.ItemCondition;
import br.com.senior.tradeit.entity.item.Item;
import br.com.senior.tradeit.entity.offer.ContactType;
import br.com.senior.tradeit.entity.offer.Offer;
import br.com.senior.tradeit.entity.offer.OfferImage;
import br.com.senior.tradeit.entity.offer.OfferStatus;
import br.com.senior.tradeit.entity.offer.view.OfferImageSummary;
import br.com.senior.tradeit.entity.offer.view.OfferVideoSummary;
import br.com.senior.tradeit.entity.user.Role;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.ForbiddenException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.AdvertisementRepository;
import br.com.senior.tradeit.repository.ItemRepository;
import br.com.senior.tradeit.repository.OfferImageRepository;
import br.com.senior.tradeit.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private MediaManagementService mediaManagementService;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OfferImageRepository offerImageRepository;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private MediaContentValidationService mediaContentValidationService;
    @InjectMocks
    private OfferService offerService;


    @Test
    void createOffer_shouldCreateOffer_whenOfferCreationDataIsValid() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password", Role.ROLE_COMMON);
        User user = new User(2L, "Jane Doe", "jane.doe@mail.com", "password", Role.ROLE_COMMON);
        Item item = new Item(1L, "item 1", null, true);
        Item tradingItem = new Item(2L, "item 2", null, true);
        BigDecimal requiredMoneyOfferAmount = new BigDecimal("10.0");
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .advertiser(advertiser)
                .item(item)
                .tradingItem(tradingItem)
                .extraMoneyAmountRequired(requiredMoneyOfferAmount)
                .status(AdvertisementStatus.ACTIVE)
                .build();
        BigDecimal additionalMoneyOffer = new BigDecimal("10.0");
        List<UUID> imageSlugs = List.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        UUID videoSlug = UUID.fromString("00000000-0000-0000-0000-000000000000");
        MultipartFile[] images = { mock(MultipartFile.class) };
        MultipartFile video = mock(MultipartFile.class);
        OfferCreationDTO offerCreation = new OfferCreationDTO(
                "offer",
                1L,
                additionalMoneyOffer,
                ItemCondition.NEW,
                images,
                video
        );

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));
        when(mediaManagementService.saveFile("videos", video)).thenReturn(videoSlug);
        when(mediaManagementService.saveFiles("images", images)).thenReturn(imageSlugs);
        doAnswer(invocation -> {
            Offer offer = invocation.getArgument(0);
            offer.setId(1L);
            return null;
        }).when(offerRepository).save(any());
        doAnswer(invocation -> {
            List<OfferImage> offerImages = invocation.getArgument(0);
            offerImages.get(0).setId(1L);
            return null;
        }).when(offerImageRepository).saveAll(any());

        OfferDetailsDTO offerDetails = offerService.createOffer(user, offerCreation);
        assertEquals(1L, offerDetails.id());
        assertEquals(2L, offerDetails.itemId());
        assertEquals("item 2", offerDetails.itemName());
        assertEquals(ItemCondition.NEW, offerDetails.itemCondition());
        assertEquals(additionalMoneyOffer, offerDetails.additionalMoneyOffer());
        assertEquals("offer", offerDetails.description());
        assertEquals(OfferStatus.PENDING, offerDetails.offerStatus());
        assertEquals("/resources/offers/images/1", offerDetails.imageUrls().get(0));
        assertEquals("/resources/offers/1/video", offerDetails.videoUrl());
        assertEquals(1L, offerDetails.advertisementId());
        assertEquals("Jane Doe", offerDetails.madeBy());

        verify(mediaContentValidationService).validate(images, video);
    }

    @ParameterizedTest
    @EnumSource(value = AdvertisementStatus.class, names = {"CANCELLED", "CLOSED"})
    void createOffer_ShouldThrowBadRequestException_whenAdvertisementIsNotActive(AdvertisementStatus status) {
        Item item = new Item(1L, "item 1", null, true);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .tradingItem(item)
                .status(status)
                .build();
        OfferCreationDTO offerCreation = new OfferCreationDTO(
                "offer",
                1L,
                null,
                null,
                null,
                null
        );

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        var exc = assertThrows(BadRequestException.class, () -> offerService.createOffer(null, offerCreation));
        assertEquals("não é possível fazer uma oferta para um anúncio não ativo", exc.getMessage());
    }

    @Test
    void createOffer_ShouldThrowBadRequestException_whenOfferUserIsAdvertiser() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com",
                "password", Role.ROLE_COMMON);
        Item item = new Item(1L, "item 1", null, true);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .tradingItem(item)
                .status(AdvertisementStatus.ACTIVE)
                .advertiser(advertiser)
                .build();
        OfferCreationDTO offerCreation = new OfferCreationDTO(
                "offer",
                1L,
                null,
                null,
                null,
                null
        );

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        var exc = assertThrows(BadRequestException.class, () -> offerService.createOffer(advertiser, offerCreation));
        assertEquals("não é possível fazer uma oferta para seus próprios anúncios", exc.getMessage());
    }

    @Test
    void createOffer_ShouldThrowBadRequestException_whenAdditionalMoneyOfferIsLessThanExpected() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com",
                "password", Role.ROLE_COMMON);
        User user = new User(2L, "Jane Doe", "jane.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Item item = new Item(1L, "item 1", null, true);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .status(AdvertisementStatus.ACTIVE)
                .advertiser(advertiser)
                .tradingItem(item)
                .extraMoneyAmountRequired(new BigDecimal("10.00"))
                .build();
        OfferCreationDTO offerCreation = new OfferCreationDTO(
                "offer",
                1L,
                new BigDecimal("5.00"),
                null,
                null,
                null
        );

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        var exc = assertThrows(BadRequestException.class, () -> offerService.createOffer(user, offerCreation));
        assertEquals("não é possível fazer uma oferta com R$5. O anúncio exige, no mínimo, uma oferta de R$10",
                exc.getMessage());
    }

    @Test
    void createOffer_ShouldThrowBadRequestException_whenAdvertisementIsNotFound() {
        OfferCreationDTO offerCreation = new OfferCreationDTO(
                "offer",
                1L,
                null,
                null,
                null,
                null
        );

        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());

        var exc = assertThrows(BadRequestException.class, () -> offerService.createOffer(null, offerCreation));
        assertEquals("não existe um anúncio com o id 1", exc.getMessage());
    }

    @Test
    void getOffersFromAdvertisement_shouldReturnAdvertisementOffersDetails_whenAdvertiserIsFoundAndUserHasPermission() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        User user = new User(2L, "Jane Doe", "jane.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Item item = new Item(1L, "item 1", null, true);
        List<OfferImage> images = List.of(new OfferImage(1L, 1L, null, null));

        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .tradingItem(item)
                .advertiser(advertiser)
                .build();
        Offer offer = Offer.builder()
                .id(1L)
                .description("offer")
                .advertisement(advertisement)
                .user(user)
                .itemCondition(ItemCondition.NEW)
                .additionalMoneyOffer(BigDecimal.ZERO)
                .createdAt(LocalDate.of(2025,1, 1).atStartOfDay())
                .status(OfferStatus.PENDING)
                .images(images)
                .build();
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));
        when(offerRepository.findByAdvertisementId(1L)).thenReturn(List.of(offer));

        List<OfferDetailsDTO> offersDetails = offerService.getOffersFromAdvertisement(advertiser, 1L);

        OfferDetailsDTO offerDetails = offersDetails.get(0);
        assertEquals(1, offersDetails.size());
        assertEquals(1L, offerDetails.id());
        assertEquals(1L, offerDetails.itemId());
        assertEquals("item 1", offerDetails.itemName());
        assertEquals(ItemCondition.NEW, offerDetails.itemCondition());
        assertEquals(BigDecimal.ZERO, offerDetails.additionalMoneyOffer());
        assertEquals("offer", offerDetails.description());
        assertEquals(OfferStatus.PENDING, offerDetails.offerStatus());
        assertEquals(1, offerDetails.imageUrls().size());
        assertEquals("/resources/offers/images/1", offerDetails.imageUrls().get(0));
        assertEquals("/resources/offers/1/video", offerDetails.videoUrl());
        assertEquals(1L, offerDetails.advertisementId());
        assertEquals("Jane Doe", offerDetails.madeBy());
    }

    @Test
    void getOffersFromAdvertisement_shouldThrownNotFoundException_whenAdvertisementIsNotFound() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);

        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                offerService.getOffersFromAdvertisement(advertiser, 1L));
    }

    @Test
    void getOffersFromAdvertisement_shouldThrownForbiddenException_whenUserIsNotAdvertiser() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        User user = new User(2L, "Jane Doe", "jane.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .advertiser(advertiser)
                .build();

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));
        assertThrows(ForbiddenException.class, () ->
                offerService.getOffersFromAdvertisement(user, 1L));
    }

    @Test
    void getOffersMadeByUser_shouldReturnOffersDetails_whenQueryIsMade() {
        User user = new User(2L, "Jane Doe", "jane.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Item item = new Item(1L, "item 1", null, true);
        List<OfferImage> images = List.of(new OfferImage(1L, 1L, null, null));

        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .tradingItem(item)
                .build();
        Offer offer = Offer.builder()
                .id(1L)
                .description("offer")
                .advertisement(advertisement)
                .user(user)
                .itemCondition(ItemCondition.NEW)
                .additionalMoneyOffer(BigDecimal.ZERO)
                .createdAt(LocalDate.of(2025,1, 1).atStartOfDay())
                .status(OfferStatus.PENDING)
                .images(images)
                .build();
        Pageable pageable = Pageable.ofSize(20);
        Page<Offer> page = new PageImpl<>(List.of(offer), pageable, 1);

        when(offerRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
        List<OfferDetailsDTO> offersDetails = offerService.getOffersMadeByUser(user, pageable)
                .stream()
                .toList();

        OfferDetailsDTO offerDetails = offersDetails.get(0);
        assertEquals(1, offersDetails.size());
        assertEquals(1L, offerDetails.id());
        assertEquals(1L, offerDetails.itemId());
        assertEquals("item 1", offerDetails.itemName());
        assertEquals(ItemCondition.NEW, offerDetails.itemCondition());
        assertEquals(BigDecimal.ZERO, offerDetails.additionalMoneyOffer());
        assertEquals("offer", offerDetails.description());
        assertEquals(OfferStatus.PENDING, offerDetails.offerStatus());
        assertEquals(1, offerDetails.imageUrls().size());
        assertEquals("/resources/offers/images/1", offerDetails.imageUrls().get(0));
        assertEquals("/resources/offers/1/video", offerDetails.videoUrl());
        assertEquals(1L, offerDetails.advertisementId());
        assertEquals("Jane Doe", offerDetails.madeBy());
    }

    @Test
    void cancelOffer_shouldOfferBeCancelled_whenUserIsOwnerAndOfferStatusIsPending() {
        User user = new User(1L, "John Doe", "john.doe@mail.com", "passeword",
                Role.ROLE_COMMON);
        Offer offer = Offer.builder()
                .id(1L)
                .status(OfferStatus.PENDING)
                .user(user)
                .build();
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        offerService.cancelOffer(user, 1L);
        assertEquals(OfferStatus.CANCELLED, offer.getStatus());
    }

    @Test
    void cancelOffer_shouldThrowForbiddenException_whenUserDoesNotOwnOffer() {
        User user = new User(1L, "John Doe", "john.doe@mail.com", "passeword",
                Role.ROLE_COMMON);
        User randomUser = new User(2L, "Jane Doe", "jane.doe@mail.com", "passeword",
                Role.ROLE_COMMON);
        Offer offer = Offer.builder()
                .id(1L)
                .status(OfferStatus.PENDING)
                .user(user)
                .build();
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        assertThrows(ForbiddenException.class, () -> offerService.cancelOffer(randomUser, 1L));
    }

    @ParameterizedTest
    @EnumSource(value = OfferStatus.class, names = {"CANCELLED", "ACCEPTED", "DENIED"})
    void cancelOffer_shouldThrowBadRequestException_whenOfferStatusIsInvalid(OfferStatus status) {
        User user = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Offer offer = Offer.builder()
                .id(1L)
                .status(status)
                .user(user)
                .build();
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        var exc = assertThrows(BadRequestException.class, () ->
                offerService.cancelOffer(user, 1L));
        assertEquals("não é possível cancelar oferta com status diferente de pendente", exc.getMessage());
    }

    @Test
    void acceptOffer_shouldOfferBeAccepted_whenUserIsAdvertiserAndIdIsFound() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .advertiser(advertiser)
                .status(AdvertisementStatus.ACTIVE)
                .build();

        Offer offer = Offer.builder()
                .id(1L)
                .status(OfferStatus.PENDING)
                .advertisement(advertisement)
                .build();
        ContactDTO contact = new ContactDTO(ContactType.WHATSAPP, "99 99999-9999");
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        offerService.acceptOffer(advertiser, 1L, contact);
        assertEquals(OfferStatus.ACCEPTED, offer.getStatus());
        assertEquals(ContactType.WHATSAPP, offer.getContactType());
        assertEquals("99 99999-9999", offer.getContactInfo());
        assertEquals(AdvertisementStatus.CLOSED, advertisement.getStatus());
        verify(offerRepository).setOtherPendingAdvertisementOffersStatusToDenied(advertisement.getId(), offer.getId());
    }

    @ParameterizedTest
    @EnumSource(value = AdvertisementStatus.class, names = {"CLOSED", "CANCELLED"})
    void acceptOffer_shouldThrowBadRequestException_whenAdvertisementStatusIsInvalid(AdvertisementStatus status) {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .advertiser(advertiser)
                .status(status)
                .build();

        Offer offer = Offer.builder()
                .id(1L)
                .status(OfferStatus.PENDING)
                .advertisement(advertisement)
                .build();
        ContactDTO contact = new ContactDTO(ContactType.WHATSAPP, "99 99999-9999");
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        var exc = assertThrows(BadRequestException.class, () ->
                offerService.acceptOffer(advertiser, 1L, contact));
        assertEquals("não é possível aceitar ofertas para um anúncio inativo", exc.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = OfferStatus.class, names = {"DENIED", "CANCELLED", "ACCEPTED"})
    void acceptOffer_shouldThrowBadRequestException_whenOfferStatusIsInvalid(OfferStatus status) {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .advertiser(advertiser)
                .status(AdvertisementStatus.ACTIVE)
                .build();

        Offer offer = Offer.builder()
                .id(1L)
                .status(status)
                .advertisement(advertisement)
                .build();
        ContactDTO contact = new ContactDTO(ContactType.WHATSAPP, "99 99999-9999");
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        var exc = assertThrows(BadRequestException.class, () ->
                offerService.acceptOffer(advertiser, 1L, contact));
        assertEquals("não é possível aceitar oferta com status diferente de pendente", exc.getMessage());
    }

    @Test
    void acceptOffer_shouldThrowForbiddenException_whenUserIsNotAdvertiser() {
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        User randomUser = new User(2L, "Jane Doe", "jane.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .advertiser(advertiser)
                .status(AdvertisementStatus.ACTIVE)
                .build();

        Offer offer = Offer.builder()
                .id(1L)
                .status(OfferStatus.PENDING)
                .advertisement(advertisement)
                .build();
        ContactDTO contact = new ContactDTO(ContactType.WHATSAPP, "99 99999-9999");
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        assertThrows(ForbiddenException.class, () ->
                offerService.acceptOffer(randomUser, 1L, contact));
    }

    @Test
    void acceptOffer_shouldThrowNotFoundException_whenOfferIsNotFound() {
        User randomUser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        ContactDTO contact = new ContactDTO(ContactType.WHATSAPP, "99 99999-9999");
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                offerService.acceptOffer(randomUser, 1L, contact));
    }

    @Test
    void getImage_shouldResolvePathAndContentType_whenOfferImageIsFound() {
        OfferImageSummary offerImageSummary = mock(OfferImageSummary.class);

        UUID imageSlug = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(offerImageSummary.getUserId()).thenReturn(1L);
        when(offerImageSummary.getAdvertiserId()).thenReturn(2L);
        when(offerImageSummary.getImageSlug())
                .thenReturn(imageSlug);
        when(offerImageSummary.getContentType())
                .thenReturn("image/jpeg");

        User user = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        when(offerImageRepository.findOfferImageById(1L)).thenReturn(Optional.of(offerImageSummary));

        offerService.getImage(user, 1L);

        verify(mediaManagementService).resolvePath("images", imageSlug);
        verify(mediaManagementService).resolveImageHttpMediaType("image/jpeg");
    }

    @Test
    void getImage_shouldThrowForbiddenException_whenUserIsNeitherAdvertiserOrOfferOwner() {
        OfferImageSummary offerImageSummary = mock(OfferImageSummary.class);
        when(offerImageSummary.getUserId()).thenReturn(1L);
        when(offerImageSummary.getAdvertiserId()).thenReturn(2L);
        User user = new User(3L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        when(offerImageRepository.findOfferImageById(1L)).thenReturn(Optional.of(offerImageSummary));

        assertThrows(ForbiddenException.class, () ->
                offerService.getImage(user, 1L));
    }

    @Test
    void getImage_shouldThrowNotFoundException_whenImageIsNotFound() {
        when(offerImageRepository.findOfferImageById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                offerService.getImage(null, 1L));
    }

    @Test
    void getVideo_shouldResolvePathAndContentType_whenOfferVideoIsFound() {
        OfferVideoSummary offerVideoSummary = mock(OfferVideoSummary.class);

        UUID videoSlug = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(offerVideoSummary.getUserId()).thenReturn(1L);
        when(offerVideoSummary.getAdvertiserId()).thenReturn(2L);
        when(offerVideoSummary.getVideoSlug())
                .thenReturn(videoSlug);
        when(offerVideoSummary.getContentType())
                .thenReturn("video/mp4");

        when(offerRepository.findVideoByOfferId(1L)).thenReturn(Optional.of(offerVideoSummary));

        User user = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);

        offerService.getVideo(user, 1L);

        verify(mediaManagementService).resolvePath("videos", videoSlug);
        verify(mediaManagementService).resolveVideoHttpMediaType("video/mp4");
    }

    @Test
    void getVideo_shouldThrowForbiddenException_whenUserIsNeitherAdvertiserOrOfferOwner() {
        OfferVideoSummary offerVideoSummary = mock(OfferVideoSummary.class);

        when(offerVideoSummary.getUserId()).thenReturn(1L);
        when(offerVideoSummary.getAdvertiserId()).thenReturn(2L);
        when(offerRepository.findVideoByOfferId(1L)).thenReturn(Optional.of(offerVideoSummary));
        User user = new User(3L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);

        assertThrows(ForbiddenException.class, () ->
                offerService.getVideo(user, 1L));
    }

    @Test
    void getVideo_shouldThrowNotFoundException_whenOfferIsNotFound() {
        when(offerRepository.findVideoByOfferId(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                offerService.getVideo(null, 1L));
    }


}