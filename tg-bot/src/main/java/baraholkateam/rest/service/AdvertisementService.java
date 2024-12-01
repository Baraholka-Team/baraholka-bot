package baraholkateam.rest.service;

import baraholkateam.rest.mapper.AdvertisementMapper;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.repository.AdvertisementRepository;
import baraholkateam.util.TagType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static baraholkateam.bot.BaraholkaBot.SEARCH_ADVERTISEMENTS_LIMIT;

/**
 * Сервис взаимодействия с сущностью "ActualAdvertisement".
 */
@Slf4j
@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    public AdvertisementEntity get(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get();
        } else {
            log.error("Actual advertisement for message id {} not found!", messageId);
            return null;
        }
    }

    public List<AdvertisementEntity> getByChatId(Long chatId) {
        return advertisementRepository.findAllByOwnerChatId(chatId);
    }

    public Long ownerChatId(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getOwnerChatId();
        } else {
            log.error("Actual advertisement's owner chat id for message id {} not found!", messageId);
            return null;
        }
    }

    public List<String> getPhotos(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getPhotoEntities();
        } else {
            log.error("Actual advertisement's photo ids for message id {} not found!", messageId);
            return null;
        }
    }

    public String getDescription(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getDescription();
        } else {
            log.error("Actual advertisement's description for message id {} not found!", messageId);
            return null;
        }
    }

    public List<String> getTags(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getTagEntities();
        } else {
            log.error("Actual advertisement's tags for message id {} not found!", messageId);
            return null;
        }
    }

    public String getTagsOfType(Long messageId, TagType tagType) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return AdvertisementMapper
                    .getAdvertisementDTO(actualAdvertisementOptional.get())
                    .getTagsOfType(tagType);
        } else {
            log.error("Actual advertisement's tags of type {} for message id {} not found!", tagType.name(), messageId);
            return null;
        }
    }

    public Long getPrice(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getPrice();
        } else {
            log.error("Actual advertisement's price for message id {} not found!", messageId);
            return null;
        }
    }

    public String getPhone(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getPhone();
        } else {
            log.error("Actual advertisement's phone for message id {} not found!", messageId);
            return null;
        }
    }

    public List<String> getContacts(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getContactEntities();
        } else {
            log.error("Actual advertisement's contacts for message id {} not found!", messageId);
            return null;
        }
    }

    public String getAdvertisementText(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return AdvertisementMapper
                    .getAdvertisementDTO(actualAdvertisementOptional.get())
                    .getAdvertisementText();
        } else {
            log.error("Actual advertisement's text for message id {} not found!", messageId);
            return null;
        }
    }

    public Long getCreationTime(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getCreationTime();
        } else {
            log.error("Actual advertisement's creation time for message id {} not found!", messageId);
            return null;
        }
    }

    public Long getNextUpdateTime(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getNextUpdateTime();
        } else {
            log.error("Actual advertisement's next update time for message id {} not found!", messageId);
            return null;
        }
    }

    public Integer getUpdateAttempt(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getUpdateAttempt();
        } else {
            log.error("Actual advertisement's update attempt for message id {} not found!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setOwnerChatId(Long messageId, Long ownerChatId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setOwnerChatId(ownerChatId);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot set owner chat id for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setDescription(Long messageId, String description) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setDescription(description);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot set description for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setPrice(Long messageId, Long price) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setPrice(price);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot set price for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setPhone(Long messageId, String phone) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setPhone(phone);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot set phone for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity addContact(Long messageId, String contact) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            AdvertisementMapper.getAdvertisementDTO(advertisementEntity).addContact(contact);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add social for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity addContacts(Long messageId, List<String> contacts) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            AdvertisementMapper.getAdvertisementDTO(advertisementEntity).addContacts(contacts);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot set socials for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity addPhoto(Long messageId, String photo) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            AdvertisementMapper.getAdvertisementDTO(advertisementEntity).addPhoto(photo);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add photo for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setPhotos(Long messageId, List<String> photos) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setPhotoEntities(photos);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add photos for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity addTags(Long messageId, List<String> tags) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            AdvertisementMapper.getAdvertisementDTO(advertisementEntity).addTags(tags);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add tags for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity addTag(Long messageId, String tag) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            AdvertisementMapper.getAdvertisementDTO(advertisementEntity).addTag(tag);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add tag for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setCreationTime(Long messageId, Long creationTime) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setCreationTime(creationTime);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add creation time for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setNextUpdateTime(Long messageId, Long nextUpdateTime) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setNextUpdateTime(nextUpdateTime);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot add next update time for message id {}!", messageId);
            return null;
        }
    }

    public AdvertisementEntity setUpdateAttempt(Long messageId, Integer updateAttempt) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            advertisementEntity.setUpdateAttempt(updateAttempt);
            return advertisementRepository.save(advertisementEntity);
        } else {
            log.error("Cannot update attempt for message id {}!", messageId);
            return null;
        }
    }

    public void insertNewAdvertisement(AdvertisementEntity advertisementEntity) {
        advertisementRepository.save(advertisementEntity);
    }

    public void removeAdvertisement(Long messageId) {
        advertisementRepository.deleteById(messageId);
    }

    public List<AdvertisementEntity> askActualAdvertisements(Long currentTime) {
        return advertisementRepository.findAllByNextUpdateTimeLessThanEqual(currentTime);
    }

    public String adText(Long messageId) {
        Optional<AdvertisementEntity> actualAdvertisementOptional = advertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            AdvertisementEntity advertisementEntity = actualAdvertisementOptional.get();
            return AdvertisementMapper
                    .getAdvertisementDTO(advertisementEntity)
                    .getAdvertisementText();
        } else {
            log.error("Cannot get advertisement text for message id {}!", messageId);
            return null;
        }
    }

    public List<AdvertisementEntity> tagsSearch(String[] tags) {
        return advertisementRepository.findAllByTagsIn(tags, SEARCH_ADVERTISEMENTS_LIMIT);
    }

}
