package baraholkateam.rest.service;

import baraholkateam.rest.model.ActualAdvertisement;
import baraholkateam.rest.model.CurrentAdvertisement;
import baraholkateam.rest.repository.ActualAdvertisementRepository;
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
public class ActualAdvertisementService {

    @Autowired
    private ActualAdvertisementRepository actualAdvertisementRepository;

    public ActualAdvertisement get(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get();
        } else {
            log.error("Actual advertisement for message id {} not found!", messageId);
            return null;
        }
    }

    public List<ActualAdvertisement> getByChatId(Long chatId) {
        return actualAdvertisementRepository.findAllByOwnerChatId(chatId);
    }

    public Long ownerChatId(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getOwnerChatId();
        } else {
            log.error("Actual advertisement's owner chat id for message id {} not found!", messageId);
            return null;
        }
    }

    public List<String> getPhotos(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getPhotos();
        } else {
            log.error("Actual advertisement's photo ids for message id {} not found!", messageId);
            return null;
        }
    }

    public String getDescription(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getDescription();
        } else {
            log.error("Actual advertisement's description for message id {} not found!", messageId);
            return null;
        }
    }

    public List<String> getTags(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getTags();
        } else {
            log.error("Actual advertisement's tags for message id {} not found!", messageId);
            return null;
        }
    }

    public String getTagsOfType(Long messageId, TagType tagType) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getTagsOfType(tagType);
        } else {
            log.error("Actual advertisement's tags of type {} for message id {} not found!", tagType.name(), messageId);
            return null;
        }
    }

    public Long getPrice(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getPrice();
        } else {
            log.error("Actual advertisement's price for message id {} not found!", messageId);
            return null;
        }
    }

    public String getPhone(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getPhone();
        } else {
            log.error("Actual advertisement's phone for message id {} not found!", messageId);
            return null;
        }
    }

    public List<String> getContacts(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getContacts();
        } else {
            log.error("Actual advertisement's contacts for message id {} not found!", messageId);
            return null;
        }
    }

    public String getAdvertisementText(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getAdvertisementText();
        } else {
            log.error("Actual advertisement's text for message id {} not found!", messageId);
            return null;
        }
    }

    public Long getCreationTime(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getCreationTime();
        } else {
            log.error("Actual advertisement's creation time for message id {} not found!", messageId);
            return null;
        }
    }

    public Long getNextUpdateTime(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getNextUpdateTime();
        } else {
            log.error("Actual advertisement's next update time for message id {} not found!", messageId);
            return null;
        }
    }

    public Integer getUpdateAttempt(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            return actualAdvertisementOptional.get().getUpdateAttempt();
        } else {
            log.error("Actual advertisement's update attempt for message id {} not found!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setOwnerChatId(Long messageId, Long ownerChatId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setOwnerChatId(ownerChatId);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot set owner chat id for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setDescription(Long messageId, String description) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setDescription(description);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot set description for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setPrice(Long messageId, Long price) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setPrice(price);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot set price for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setPhone(Long messageId, String phone) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setPhone(phone);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot set phone for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement addSocial(Long messageId, String social) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.addSocial(social);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add social for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setSocials(Long messageId, List<String> socials) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setSocials(socials);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot set socials for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement addPhoto(Long messageId, String photo) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.addPhotos(photo);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add photo for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setPhotos(Long messageId, List<String> photos) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setPhotos(photos);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add photos for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement addTags(Long messageId, List<String> tags) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.addTags(tags);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add tags for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement addTag(Long messageId, String tag) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.addTag(tag);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add tag for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setCreationTime(Long messageId, Long creationTime) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setCreationTime(creationTime);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add creation time for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setNextUpdateTime(Long messageId, Long nextUpdateTime) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setNextUpdateTime(nextUpdateTime);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot add next update time for message id {}!", messageId);
            return null;
        }
    }

    public ActualAdvertisement setUpdateAttempt(Long messageId, Integer updateAttempt) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            advertisement.setUpdateAttempt(updateAttempt);
            return actualAdvertisementRepository.save(advertisement);
        } else {
            log.error("Cannot update attempt for message id {}!", messageId);
            return null;
        }
    }

    public void insertNewAdvertisement(CurrentAdvertisement currentAdvertisement) {
        ActualAdvertisement actualAdvertisement = new ActualAdvertisement(currentAdvertisement.getChatId())
                .setMessageId(currentAdvertisement.getMessageId())
                .setPhotos(currentAdvertisement.getPhotos())
                .setDescription(currentAdvertisement.getDescription())
                .setPrice(currentAdvertisement.getPrice())
                .addTags(currentAdvertisement.getTags())
                .setPhone(currentAdvertisement.getPhone())
                .setSocials(currentAdvertisement.getContacts())
                .setCreationTime(currentAdvertisement.getCreationTime())
                .setNextUpdateTime(currentAdvertisement.getNextUpdateTime())
                .setUpdateAttempt(currentAdvertisement.getUpdateAttempt());
        actualAdvertisementRepository.save(actualAdvertisement);
    }

    public void removeAdvertisement(Long messageId) {
        actualAdvertisementRepository.deleteById(messageId);
    }

    public List<ActualAdvertisement> askActualAdvertisements(Long currentTime) {
        return actualAdvertisementRepository.findAllByNextUpdateTimeLessThanEqual(currentTime);
    }

    public String adText(Long messageId) {
        Optional<ActualAdvertisement> actualAdvertisementOptional = actualAdvertisementRepository.findById(messageId);
        if (actualAdvertisementOptional.isPresent()) {
            ActualAdvertisement advertisement = actualAdvertisementOptional.get();
            return advertisement.getAdvertisementText();
        } else {
            log.error("Cannot get advertisement text for message id {}!", messageId);
            return null;
        }
    }

    public List<ActualAdvertisement> tagsSearch(String[] tags) {
        return actualAdvertisementRepository.findAllByTagsIn(tags, SEARCH_ADVERTISEMENTS_LIMIT);
    }

}
