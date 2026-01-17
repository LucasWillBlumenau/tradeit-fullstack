package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.advertisement.AdvertisementCreationDTO;
import br.com.senior.tradeit.dto.advertisement.AdvertisementDetailsDTO;
import br.com.senior.tradeit.dto.advertisement.AdvertisementSummaryDTO;
import br.com.senior.tradeit.entity.advertisement.Advertisement;
import br.com.senior.tradeit.entity.advertisement.AdvertisementImage;
import br.com.senior.tradeit.entity.advertisement.AdvertisementStatus;
import br.com.senior.tradeit.entity.advertisement.view.AdvertisementSummary;
import br.com.senior.tradeit.entity.category.Category;
import br.com.senior.tradeit.entity.condition.ItemCondition;
import br.com.senior.tradeit.entity.item.Item;
import br.com.senior.tradeit.entity.user.Role;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.ForbiddenException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.AdvertisementImageRepository;
import br.com.senior.tradeit.repository.AdvertisementRepository;
import br.com.senior.tradeit.repository.ItemRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {

    @Mock
    private MediaManagementService mediaManagementService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private AdvertisementImageRepository advertisementImageRepository;
    @Mock
    private MediaContentValidationService mediaContentValidationService;
    @InjectMocks
    private AdvertisementService advertisementService;

    private ProjectionFactory projectionFactory;

    @BeforeEach
    void setUp() {
        projectionFactory = new SpelAwareProxyProjectionFactory();
    }

    @Test
    void getAdvertisements_shouldReturnAdvertisementsSummaries_whenThereAreAdvertisements() {
        Pageable pageable = Pageable.ofSize(20);
        List<AdvertisementSummary> advertisements = List.of(
                createAdvertisement(1L, "desc 1", 1L, "item 1", 1L, 1L),
                createAdvertisement(2L, "desc 2", 2L, "item 2", 2L, 1L)
        );
        Page<AdvertisementSummary> advertisementsPage = new PageImpl<>(advertisements, pageable, 2);

        when(advertisementRepository.findAllActive(pageable)).thenReturn(advertisementsPage);
        List<AdvertisementSummaryDTO> advertisementSummaries = advertisementService.getAdvertisements(pageable)
                .stream()
                .toList();

        assertEquals(2, advertisementSummaries.size());
        assertEquals(1L, advertisementSummaries.get(0).id());
        assertEquals("desc 1", advertisementSummaries.get(0).description());
        assertEquals(1L, advertisementSummaries.get(0).item().id());
        assertEquals("item 1", advertisementSummaries.get(0).item().name());
        assertEquals(1L, advertisementSummaries.get(0).item().categoryId());
        assertEquals("/resources/advertisements/1/images/1", advertisementSummaries.get(0).imageUrl());
        assertEquals(2L, advertisementSummaries.get(1).id());
        assertEquals("desc 2", advertisementSummaries.get(1).description());
        assertEquals(2L, advertisementSummaries.get(1).item().id());
        assertEquals("item 2", advertisementSummaries.get(1).item().name());
        assertEquals(2L, advertisementSummaries.get(1).item().categoryId());
        assertEquals("/resources/advertisements/2/images/1", advertisementSummaries.get(1).imageUrl());
    }

    @Test
    void  getAdvertisements_shouldReturnEmptyPage_whenThereIsNoAdvertisement() {
        Pageable pageable = Pageable.ofSize(20);
        List<AdvertisementSummary> advertisements = List.of();
        Page<AdvertisementSummary> advertisementsPage = new PageImpl<>(advertisements, pageable, 0);

        when(advertisementRepository.findAllActive(pageable)).thenReturn(advertisementsPage);
        List<AdvertisementSummaryDTO> advertisementSummaries = advertisementService.getAdvertisements(pageable)
                .stream()
                .toList();
        assertEquals(0, advertisementSummaries.size());
    }

    private AdvertisementSummary createAdvertisement(
            Long id,
            String description,
            Long itemId,
            String itemName,
            Long categoryId,
            Long displayOrder
    ) {
        AdvertisementSummary advertisementSummary = projectionFactory.createProjection(AdvertisementSummary.class);
        advertisementSummary.setId(id);
        advertisementSummary.setDescription(description);
        advertisementSummary.setItemId(itemId);
        advertisementSummary.setItemName(itemName);
        advertisementSummary.setCategoryId(categoryId);
        advertisementSummary.setDisplayOrder(displayOrder);
        return advertisementSummary;
    }

    @Test
    void getAdvertisement_shouldReturnAdvertisementDetails_whenAdvertisementIsFound() {
        Category category1 = new Category(1L, "category 1", true);
        Category category2 = new Category(2L, "category 2", true);
        Item item = new Item(1L, "item 1" , category1, true);
        Item tradingItem = new Item(2L, "item 2", category2, true);
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "secretpassword",
                Role.ROLE_ADMIN);
        UUID videoSlug = UUID.fromString("00000000-0000-0000-0000-000000000000");
        LocalDateTime createdAt = LocalDate.of(2025, 1, 1)
                .atStartOfDay();
        BigDecimal extraMoneyAmountRequired = new BigDecimal("10.00");
        List<AdvertisementImage> images = List.of(
                AdvertisementImage.builder().displayOrder(1L).build(),
                AdvertisementImage.builder().displayOrder(2L).build(),
                AdvertisementImage.builder().displayOrder(3L).build()
        );

        Advertisement advertisement = new Advertisement(
                1L,
                item,
                advertiser,
                tradingItem,
                extraMoneyAmountRequired,
                "advertisement description",
                AdvertisementStatus.ACTIVE,
                images,
                ItemCondition.NEW,
                videoSlug,
                "video/mp4",
                createdAt
        );

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        AdvertisementDetailsDTO advertisementDetails = advertisementService.getAdvertisement(1L);

        assertEquals(1L, advertisementDetails.id());
        assertEquals("advertisement description", advertisementDetails.description());
        assertEquals(1L, advertisementDetails.item().id());
        assertEquals("item 1", advertisementDetails.item().name());
        assertEquals(1L, advertisementDetails.item().categoryId());
        assertEquals(2L, advertisementDetails.tradingItem().id());
        assertEquals("item 2", advertisementDetails.tradingItem().name());
        assertEquals(2L, advertisementDetails.tradingItem().categoryId());
        assertEquals(createdAt.toLocalDate(), advertisementDetails.advertisementDate());
        assertEquals("John Doe", advertisementDetails.advertiserName());
        assertEquals(extraMoneyAmountRequired, advertisementDetails.extraMoneyAmountRequired());
        assertEquals("/resources/advertisements/1/images/1", advertisementDetails.imageUrls().get(0));
        assertEquals("/resources/advertisements/1/images/2", advertisementDetails.imageUrls().get(1));
        assertEquals("/resources/advertisements/1/images/3", advertisementDetails.imageUrls().get(2));
        assertEquals("/resources/advertisements/1/video", advertisementDetails.videoUrl());
    }

    @Test
    void getAdvertisement_shouldRaiseNotFoundException_whenAdvertisementIsNotFound() {
        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> advertisementService.getAdvertisement(1L));
    }

    @Test
    void createAdvertisement_shouldBeCreated_whenAdvertisementCreationDataIsValid() {
        // Given
        User user = new User(
                1L,"John Doe","john.doe@mail.com","password",Role.ROLE_COMMON);
        Category category1 = new Category(1L, "category 1", true);
        Category category2 = new Category(2L, "category 2", true);
        Item item1 = new Item(1L, "item 1", category1, true);
        Item item2 = new Item(2L, "item 2", category2, true);
        List<UUID> imageIds = List.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        MultipartFile[] images = { mock(MultipartFile.class) };
        MultipartFile video = mock(MultipartFile.class);

        AdvertisementCreationDTO advertisementCreation = new AdvertisementCreationDTO(
                "description",
                1L,
                ItemCondition.NEW,
                new BigDecimal("10.0"),
                2L,
                images,
                video
        );
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(item2));
        when(mediaManagementService.saveFiles("images", images)).thenReturn(imageIds);

        // When
        advertisementService.createAdvertisement(user, advertisementCreation);

        // Then
        verify(mediaContentValidationService).validate(images, video);
        verify(mediaManagementService).saveFile("videos", video);
        verify(advertisementRepository).save(any());
        verify(mediaManagementService).saveFiles("images", images);
        verify(advertisementImageRepository).saveAll(any());
    }

    @Test
    void createAdvertisement_shouldThrowBadRequestException_whenItemIsNotFound() {
        // Given
        User user = new User(
                1L,"John Doe","john.doe@mail.com","password",Role.ROLE_COMMON);
        Category category = new Category(2L, "category 2", true);
        Item item = new Item(2L, "item 2", category, true);
        MultipartFile[] images = { mock(MultipartFile.class) };
        MultipartFile video = mock(MultipartFile.class);
        AdvertisementCreationDTO advertisementCreation = new AdvertisementCreationDTO(
                "description",
                1L,
                ItemCondition.NEW,
                new BigDecimal("10.0"),
                2L,
                images,
                video
        );
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());
        // When -> Then
        BadRequestException exc = assertThrows(BadRequestException.class, () -> advertisementService.createAdvertisement(
                user,
                advertisementCreation
        ));
        assertEquals("item de id 1 não existe", exc.getMessage());
    }

    @Test
    void createAdvertisement_shouldThrowBadRequestException_whenTradingItemIsNotFound() {
        // Given
        User user = new User(
                1L,"John Doe","john.doe@mail.com","password",Role.ROLE_COMMON);
        Category category = new Category(1L, "category 1", true);
        Item item = new Item(1L, "item 1", category, true);
        MultipartFile[] images = { mock(MultipartFile.class) };
        MultipartFile video = mock(MultipartFile.class);
        AdvertisementCreationDTO advertisementCreation = new AdvertisementCreationDTO(
                "description",
                1L,
                ItemCondition.NEW,
                new BigDecimal("10.0"),
                2L,
                images,
                video
        );
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.empty());

        // When -> Then
        BadRequestException exc = assertThrows(BadRequestException.class, () -> advertisementService.createAdvertisement(
                user,
                advertisementCreation
        ));
        assertEquals("item de id 2 não existe", exc.getMessage());
    }

    @Test
    void cancelAdvertisement_shouldCancelAdvertisement_whenAdvertisementCanBeCancelled() {
        // Given
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password", Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .advertiser(advertiser)
                .status(AdvertisementStatus.ACTIVE)
                .build();
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        // When
        advertisementService.cancelAdvertisement(advertiser, 1L);

        // Then
        assertEquals(AdvertisementStatus.CANCELLED, advertisement.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = AdvertisementStatus.class, names = {"CLOSED", "CANCELLED"})
    void cancelAdvertisement_shouldThrowBadRequestError_whenAdvertisementStatusIsInvalid(AdvertisementStatus status) {
        // Given
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .advertiser(advertiser)
                .status(status)
                .build();
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        // When
        BadRequestException exc = assertThrows(BadRequestException.class,
                () -> advertisementService.cancelAdvertisement(advertiser, 1L));

        // Then
        assertEquals("só é possível inativar anúncios com o status ativo", exc.getMessage());
    }

    @Test
    void cancelAdvertisement_shouldThrowForbiddenException_whenUserIsNotTheAdvertiser() {
        // Given
        User advertiser = new User(1L, "John Doe", "john.doe@mail.com", "password",
                Role.ROLE_COMMON);
        User randomUser = new User(2L, "Jane Doe", "jane.doe@mail.com", "password",
                Role.ROLE_COMMON);
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .advertiser(advertiser)
                .status(AdvertisementStatus.ACTIVE)
                .build();
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        // When -> Then
        assertThrows(ForbiddenException.class, () -> advertisementService.cancelAdvertisement(randomUser, 1L));
    }

    @Test
    void getAdvertisementImage_shouldResolveMediaPathAndType_whenImageIsFound() {
        UUID imageSlug = UUID.fromString("00000000-0000-0000-0000-000000000000");
        AdvertisementImage image = new AdvertisementImage(
                1L,
                1L,
                imageSlug,
                "image/png"
        );
        when(advertisementImageRepository.findByDisplayOrderAndAdvertisementId(1L, 1L))
                .thenReturn(Optional.of(image));

        advertisementService.getAdvertisementImage(1L, 1L);

        verify(mediaManagementService).resolvePath("images", imageSlug);
        verify(mediaManagementService).resolveImageHttpMediaType("image/png");
    }

    @Test
    void getAdvertisementImage_shouldThrowNotFoundException_whenImageIsNotFound() {
        when(advertisementImageRepository.findByDisplayOrderAndAdvertisementId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                advertisementService.getAdvertisementImage(1L, 1L));
    }

    @Test
    void getAdvertisementVideo_shouldResolveMediaPathAndType_whenAdvertisementIsFound() {
        // Given
        UUID imageSlug = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Advertisement advertisement = Advertisement.builder()
                .id(1L)
                .videoSlug(imageSlug)
                .videoContentType("video/mp4")
                .build();
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        // When
        advertisementService.getAdvertisementVideo(1L);

        // Then
        verify(mediaManagementService).resolvePath("videos", imageSlug);
        verify(mediaManagementService).resolveVideoHttpMediaType("video/mp4");
    }

    @Test
    void getAdvertisementVideo_shouldThrowNotFoundException_whenAdvertisementIsNotFound() {
        // Given
        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());

        // When -> Then
        assertThrows(NotFoundException.class, () -> advertisementService.getAdvertisementVideo(1L));
    }
}