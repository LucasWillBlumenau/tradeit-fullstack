package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.media.MediaDTO;
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
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.ForbiddenException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OfferService {

    private final MediaManagementService mediaManagementService;
    private final OfferRepository offerRepository;
    private final OfferImageRepository offerImageRepository;
    private final AdvertisementRepository advertisementRepository;
    private final MediaContentValidationService mediaContentValidationService;

    public OfferService(
            MediaManagementService mediaManagementService,
            OfferRepository offerRepository,
            OfferImageRepository offerImageRepository,
            AdvertisementRepository advertisementRepository,
            MediaContentValidationService mediaContentValidationService
    ) {
        this.mediaManagementService = mediaManagementService;
        this.offerRepository = offerRepository;
        this.offerImageRepository = offerImageRepository;
        this.advertisementRepository = advertisementRepository;
        this.mediaContentValidationService = mediaContentValidationService;
    }

    @Transactional
    public OfferDetailsDTO createOffer(User user, OfferCreationDTO offerCreationDTO) {
        mediaContentValidationService.validate(offerCreationDTO.images(), offerCreationDTO.video());

        Long advertisementId = offerCreationDTO.advertisementId();
        Advertisement advertisement = findAdvertisementById(advertisementId);

        if (advertisement.getStatus() != AdvertisementStatus.ACTIVE) {
            throw new BadRequestException("não é possível fazer uma oferta para um anúncio não ativo");
        }
        if (user.equals(advertisement.getAdvertiser())) {
            throw new BadRequestException("não é possível fazer uma oferta para seus próprios anúncios");
        }

        if (advertisement.getExtraMoneyAmountRequired().compareTo(offerCreationDTO.additionalMoneyOffer()) > 0) {
            NumberFormat format = NumberFormat.getNumberInstance();
            String required = format.format(advertisement.getExtraMoneyAmountRequired());
            String offered = format.format(offerCreationDTO.additionalMoneyOffer());
            String error = String.format(
                    "não é possível fazer uma oferta com R$%s. O anúncio exige, no mínimo, uma oferta de R$%s",
                    offered,
                    required
            );
            throw new BadRequestException(error);
        }

        UUID videoSlug = mediaManagementService.saveFile("videos", offerCreationDTO.video());
        LocalDateTime now = LocalDateTime.now();
        Offer offer = Offer.builder()
                .description(offerCreationDTO.description())
                .additionalMoneyOffer(offerCreationDTO.additionalMoneyOffer())
                .advertisement(advertisement)
                .user(user)
                .createdAt(now)
                .videoSlug(videoSlug)
                .videoContentType(offerCreationDTO.video().getContentType())
                .status(OfferStatus.PENDING)
                .itemCondition(offerCreationDTO.itemCondition())
                .build();
        offerRepository.save(offer);
        List<OfferImage> images = saveImages(offerCreationDTO, offer);

        offer.setImages(images);
        return mapOfferToOfferDetailsDTO(offer);
    }

    private Advertisement findAdvertisementById(Long advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new BadRequestException("não existe um anúncio com o id " + advertisementId));
    }

    private List<OfferImage> saveImages(OfferCreationDTO offerCreationDTO, Offer offer) {
        var files = offerCreationDTO.images();
        var imageIds = mediaManagementService.saveFiles("images", files);
        List<OfferImage> images = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            UUID fileId = imageIds.get(i);
            String contentType = files[i].getContentType();
            Long offerId = offer.getId();
            OfferImage offerImage = OfferImage.builder()
                    .offerId(offerId)
                    .imageSlug(fileId)
                    .contentType(contentType)
                    .build();
            images.add(offerImage);
        }
        offerImageRepository.saveAll(images);
        return images;
    }

    public List<OfferDetailsDTO> getOffersFromAdvertisement(User user, Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(NotFoundException::new);
        if (!user.equals(advertisement.getAdvertiser())) {
            throw new ForbiddenException();
        }
        return offerRepository.findByAdvertisementId(advertisementId)
                .stream()
                .map(this::mapOfferToOfferDetailsDTO)
                .toList();
    }

    public Page<OfferDetailsDTO> getOffersMadeByUser(User user, Pageable pageable) {
        return offerRepository.findByUserId(user.getId(), pageable)
                .map(this::mapOfferToOfferDetailsDTO);

    }

    private OfferDetailsDTO mapOfferToOfferDetailsDTO(Offer offer) {
        Advertisement advertisement = offer.getAdvertisement();
        Item item = advertisement.getTradingItem();
        Long id = offer.getId();
        Long itemId = item.getId();
        String itemName = item.getName();
        ItemCondition itemCondition = offer.getItemCondition();
        BigDecimal additionalMoneyOffer = offer.getAdditionalMoneyOffer();
        String description = offer.getDescription();
        OfferStatus offerStatus = offer.getStatus();
        List<String> imageUrls = offer.getImages()
                .stream()
                .map(offerImage -> String.format("/resources/offers/images/%d",
                        offerImage.getId()))
                .toList();
        String videoUrl = String.format("/resources/offers/%d/video", offer.getId());
        String madeBy = offer.getUser().getName();
        ContactType contactType = offer.getContactType();
        String contactInfo = offer.getContactInfo();
        Long advertisementId = advertisement.getId();
        OfferStatus status = offer.getStatus();
        Optional<ContactDTO> contact = wrapContactInformation(contactType, contactInfo);
        String advertisementImageUrl = String.format(
                "/resources/advertisements/%d/images/%d",
                advertisementId,
                advertisement.getImages().get(0).getDisplayOrder()
        );
        return new OfferDetailsDTO(
                id,
                itemId,
                itemName,
                itemCondition,
                additionalMoneyOffer,
                description,
                offerStatus,
                imageUrls,
                videoUrl,
                madeBy,
                advertisementId,
                status,
                contact,
                advertisementImageUrl
        );
    }

    private Optional<ContactDTO> wrapContactInformation(ContactType contactType, String contactInfo) {
        if (contactType != null && contactInfo != null) {
            return Optional.of(new ContactDTO(contactType, contactInfo));
        }
        return Optional.empty();
    }

    @Transactional
    public void cancelOffer(User user, Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(NotFoundException::new);
        if (!user.equals(offer.getUser())) {
            throw new ForbiddenException();
        }
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadRequestException("não é possível cancelar oferta com status diferente de pendente");
        }
        offer.setStatus(OfferStatus.CANCELLED);
    }

    @Transactional
    public void acceptOffer(User user, Long id, ContactDTO contact) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        Advertisement advertisement = offer.getAdvertisement();
        if (!user.equals(advertisement.getAdvertiser())) {
            throw new ForbiddenException();
        }
        if (advertisement.getStatus() != AdvertisementStatus.ACTIVE) {
            throw new BadRequestException("não é possível aceitar ofertas para um anúncio inativo");
        }
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadRequestException("não é possível aceitar oferta com status diferente de pendente");
        }

        offerRepository.setOtherPendingAdvertisementOffersStatusToDenied(advertisement.getId(), offer.getId());
        offer.setStatus(OfferStatus.ACCEPTED);
        offer.setContactType(contact.contactType());
        offer.setContactInfo(contact.contactInfo());
        advertisement.setStatus(AdvertisementStatus.CLOSED);
    }

    public MediaDTO getImage(User user, Long imageId) {
        OfferImageSummary imageSummary = offerImageRepository.findOfferImageById(imageId)
                .orElseThrow(NotFoundException::new);

        validateThatUserCanAccessMedia(user, imageSummary.getUserId(), imageSummary.getAdvertiserId());
        Resource resource = mediaManagementService.resolvePath("images", imageSummary.getImageSlug());
        MediaType mediaType = mediaManagementService.resolveImageHttpMediaType(imageSummary.getContentType());
        return new MediaDTO(resource, mediaType);
    }

    public MediaDTO getVideo(User user, Long videoId) {
        OfferVideoSummary videoSummary = offerRepository.findVideoByOfferId(videoId)
                .orElseThrow(NotFoundException::new);
        validateThatUserCanAccessMedia(user, videoSummary.getUserId(), videoSummary.getAdvertiserId());
        Resource resource = mediaManagementService.resolvePath("videos", videoSummary.getVideoSlug());
        MediaType mediaType = mediaManagementService.resolveVideoHttpMediaType(videoSummary.getContentType());
        return new MediaDTO(resource, mediaType);
    }

    private static void validateThatUserCanAccessMedia(User user, Long userId, Long advertiserId) {
        boolean userOwnsAdvertisementOrOffer = userId.equals(user.getId()) || advertiserId.equals(user.getId());
        if (!userOwnsAdvertisementOrOffer) {
            throw new ForbiddenException();
        }
    }
}
