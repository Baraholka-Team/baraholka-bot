package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.model.ContactEntity;
import baraholkateam.rest.model.PhotoEntity;
import baraholkateam.rest.model.TagEntity;

import java.util.List;

/**
 * Маппер бизнес сущности объявления и JPA сущности
 */
public class AdvertisementMapper {

    public static AdvertisementEntity getAdvertisementEntity(AdvertisementDTO advertisementDTO) {
        List<PhotoEntity> photoEntityList = PhotoMapper.getPhotoEntityList(advertisementDTO.getPhotos());
        List<TagEntity> tagEntityList = TagMapper.getTagEntityList(advertisementDTO.getTags());
        List<ContactEntity> contactEntityList = ContactMapper.getContactEntityList(advertisementDTO.getContacts());
        return AdvertisementEntity.builder()
                .advertisementId(advertisementDTO.getAdvertisementId())
                .chatId(advertisementDTO.getChatId())
                .messageId(advertisementDTO.getMessageId())
                .userId(advertisementDTO.getUserId())
                .photos(photoEntityList)
                .description(advertisementDTO.getDescription())
                .tags(tagEntityList)
                .price(advertisementDTO.getPrice())
                .contacts(contactEntityList)
                .creationTime(advertisementDTO.getCreationTime())
                .nextUpdateTime(advertisementDTO.getNextUpdateTime())
                .updateAttempt(advertisementDTO.getUpdateAttempt())
                .build();
    }

    public static AdvertisementDTO getAdvertisementDTO(AdvertisementEntity advertisementEntity) {
        List<PhotoDTO> photoDTOList = PhotoMapper.getPhotoDTOList(advertisementEntity.getPhotos());
        List<TagDTO> tagDTOList = TagMapper.getTagDTOList(advertisementEntity.getTags());
        List<ContactDTO> contactDTOList = ContactMapper.getContactDTOList(advertisementEntity.getContacts());
        return AdvertisementDTO.builder()
                .advertisementId(advertisementEntity.getAdvertisementId())
                .chatId(advertisementEntity.getChatId())
                .messageId(advertisementEntity.getMessageId())
                .userId(advertisementEntity.getUserId())
                .photos(photoDTOList)
                .description(advertisementEntity.getDescription())
                .tags(tagDTOList)
                .price(advertisementEntity.getPrice())
                .contacts(contactDTOList)
                .creationTime(advertisementEntity.getCreationTime())
                .nextUpdateTime(advertisementEntity.getNextUpdateTime())
                .updateAttempt(advertisementEntity.getUpdateAttempt())
                .build();
    }

}
