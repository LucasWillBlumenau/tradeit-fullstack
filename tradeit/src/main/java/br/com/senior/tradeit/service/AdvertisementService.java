package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.advertisement.AdvertisementCreationDTO;
import br.com.senior.tradeit.dto.advertisement.AdvertisementDetailsDTO;
import br.com.senior.tradeit.dto.advertisement.AdvertisementSummaryDTO;
import br.com.senior.tradeit.dto.item.ItemDetailsDTO;
import br.com.senior.tradeit.dto.media.MediaDTO;
import br.com.senior.tradeit.entity.advertisement.Advertisement;
import br.com.senior.tradeit.entity.advertisement.AdvertisementImage;
import br.com.senior.tradeit.entity.advertisement.AdvertisementStatus;
import br.com.senior.tradeit.entity.advertisement.view.AdvertisementSummary;
import br.com.senior.tradeit.entity.condition.ItemCondition;
import br.com.senior.tradeit.entity.item.Item;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.ForbiddenException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.AdvertisementImageRepository;
import br.com.senior.tradeit.repository.AdvertisementRepository;
import br.com.senior.tradeit.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class AdvertisementService {

    private final MediaManagementService mediaManagementService;
    private final ItemRepository itemRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementImageRepository advertisementImageRepository;
    private final MediaContentValidationService mediaContentValidationService;

    public AdvertisementService(
            MediaManagementService mediaManagementService,
            ItemRepository itemRepository,
            AdvertisementRepository advertisementRepository,
            AdvertisementImageRepository advertisementImageRepository,
            MediaContentValidationService mediaContentValidationService
    ) {
        this.mediaManagementService = mediaManagementService;
        this.itemRepository = itemRepository;
        this.advertisementRepository = advertisementRepository;
        this.advertisementImageRepository = advertisementImageRepository;
        this.mediaContentValidationService = mediaContentValidationService;
    }

    public Page<AdvertisementSummaryDTO> getAdvertisements(Pageable pageable) {
        return advertisementRepository.findAllActive(pageable)
                .map(this::mapAdvertisementSummaryToDTO);
    }

    public List<AdvertisementSummaryDTO> getUserAdvertisements(User user) {
        return advertisementRepository.findByAdvertiserId(user.getId())
                .stream()
                .map(this::mapAdvertisementSummaryToDTO)
                .toList();
    }

    private AdvertisementSummaryDTO mapAdvertisementSummaryToDTO(AdvertisementSummary advertisement) {
        var id = advertisement.getId();
        var description = advertisement.getDescription();
        var itemId = advertisement.getItemId();
        var itemName = advertisement.getItemName();
        var categoryId = advertisement.getCategoryId();
        var tradingItemId = advertisement.getTradingItemId();
        var tradingItemName = advertisement.getTradingItemName();
        var tradingCategoryId = advertisement.getTradingCategoryId();
        var itemDetail = new ItemDetailsDTO(itemId, itemName, categoryId);
        var tradingItemDetail = new ItemDetailsDTO(tradingItemId, tradingItemName, tradingCategoryId);
        var extraMoneyAmountRequired = advertisement.getExtraMoneyAmountRequired();
        var imageUrl = String.format(
                "/resources/advertisements/%d/images/%d",
                advertisement.getId(),
                advertisement.getDisplayOrder()
        );
        var condition = advertisement.getItemCondition();
        return new AdvertisementSummaryDTO(
                id,
                description,
                itemDetail,
                tradingItemDetail,
                extraMoneyAmountRequired,
                imageUrl,
                condition
        );
    }


    public AdvertisementDetailsDTO getAdvertisement(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return mapAdvertisementToDTO(advertisement);
    }

    private AdvertisementDetailsDTO mapAdvertisementToDTO(Advertisement advertisement) {
        String description = advertisement.getDescription();
        ItemDetailsDTO itemDetails = new ItemDetailsDTO(advertisement.getItem());
        ItemDetailsDTO traidingItemDetails = new ItemDetailsDTO(advertisement.getTradingItem());
        LocalDate advertisementDate = advertisement.getCreatedAt().toLocalDate();
        String advertiserName = advertisement.getAdvertiser().getName();
        BigDecimal extraMoneyAmountRequired = advertisement.getExtraMoneyAmountRequired();
        List<String> imageUrls = advertisement.getImages()
                .stream()
                .map(image -> String.format(
                        "/resources/advertisements/%d/images/%d",
                        advertisement.getId(),
                        image.getDisplayOrder()
                ))
                .toList();

        String videoUrl = String.format("/resources/advertisements/%d/video", advertisement.getId());
        AdvertisementStatus status = advertisement.getStatus();
        ItemCondition condition = advertisement.getItemCondition();
        return new AdvertisementDetailsDTO(
                advertisement.getId(),
                description,
                itemDetails,
                traidingItemDetails,
                advertisementDate,
                advertiserName,
                extraMoneyAmountRequired,
                imageUrls,
                videoUrl,
                status,
                condition
        );
    }

    @Transactional
    public void createAdvertisement(
            User user,
            AdvertisementCreationDTO advertisementCreationDTO
    ) {
        mediaContentValidationService.validate(advertisementCreationDTO.images(), advertisementCreationDTO.video());
        var item = findItemById(advertisementCreationDTO.itemId());
        var tradingItem = findItemById(advertisementCreationDTO.tradingItemId());
        var videoId = mediaManagementService.saveFile(
                "videos",
                advertisementCreationDTO.video()
        );
        var now = LocalDateTime.now();

        Advertisement advertisement = Advertisement.builder()
                .description(advertisementCreationDTO.description())
                .status(AdvertisementStatus.ACTIVE)
                .item(item)
                .itemCondition(advertisementCreationDTO.itemCondition())
                .tradingItem(tradingItem)
                .extraMoneyAmountRequired(advertisementCreationDTO.extraMoneyAmountRequired())
                .advertiser(user)
                .createdAt(now)
                .videoSlug(videoId)
                .videoContentType(advertisementCreationDTO.video().getContentType())
                .build();
        advertisementRepository.save(advertisement);
        saveImages(advertisementCreationDTO, advertisement);
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findByIdAndIsActiveTrue(itemId)
                .orElseThrow(() -> new BadRequestException("item de id " + itemId + " não existe"));
    }

    private void saveImages(AdvertisementCreationDTO advertisementCreationDTO, Advertisement advertisement) {
        MultipartFile[] files = advertisementCreationDTO.images();
        var imageIds = mediaManagementService.saveFiles("images", files);
        List<AdvertisementImage> images = new ArrayList<>();
        for (int i = 0; i < imageIds.size(); i++) {
            long displayOrder = i + 1;
            var image = new AdvertisementImage(
                    displayOrder,
                    advertisement.getId(),
                    imageIds.get(i),
                    files[i].getContentType()
            );
            images.add(image);
        }
        advertisementImageRepository.saveAll(images);
    }

    @Transactional
    public void cancelAdvertisement(User user, Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        if (!user.equals(advertisement.getAdvertiser())) {
            throw new ForbiddenException();
        }
        if (advertisement.getStatus() != AdvertisementStatus.ACTIVE) {
            throw new BadRequestException("só é possível inativar anúncios com o status ativo");
        }
        advertisement.setStatus(AdvertisementStatus.CANCELLED);
    }

    public MediaDTO getAdvertisementImage(Long advertisementId, Long displayOrder) {
        AdvertisementImage image = advertisementImageRepository.findByDisplayOrderAndAdvertisementId(
                        displayOrder,
                        advertisementId
                )
                .orElseThrow(NotFoundException::new);
        Resource resource = mediaManagementService.resolvePath("images", image.getImageSlug());
        MediaType mediaType = mediaManagementService.resolveImageHttpMediaType(image.getContentType());
        return new MediaDTO(resource, mediaType);
    }

    public MediaDTO getAdvertisementVideo(Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(NotFoundException::new);
        Resource resource = mediaManagementService.resolvePath("videos", advertisement.getVideoSlug());
        MediaType mediaType = mediaManagementService.resolveVideoHttpMediaType(advertisement.getVideoContentType());
        return new MediaDTO(resource, mediaType);
    }
}
