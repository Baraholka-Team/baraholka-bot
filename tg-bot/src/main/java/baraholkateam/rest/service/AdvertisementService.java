package baraholkateam.rest.service;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.mapper.AdvertisementMapper;
import baraholkateam.rest.mapper.TagMapper;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.model.AdvertisementEntityId;
import baraholkateam.rest.repository.AdvertisementRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис взаимодействия с объявлениями пользователей
 */
@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Value("${search_advertisement_limit}")
    private Integer searchAdvertisementsLimit;

    /**
     * Получает объявление пользователя по id объявления
     * @param chatId id чата
     * @param messageId id сообщения в рамках чата
     * @return объявление пользователя или null, если объявление не найдено
     */
    public AdvertisementDTO getAdvertisement(@NotNull Long chatId, @NotNull Long messageId) {
        return advertisementRepository.findById(new AdvertisementEntityId(chatId, messageId))
                .map(AdvertisementMapper::getAdvertisementDTO)
                .orElse(null);
    }

    /**
     * Получает все объявления пользователя из чата
     * @param chatId id чата
     * @param userId id пользователя
     * @return все объявления пользователя
     */
    public List<AdvertisementDTO> getUserAdvertisements(@NotNull Long chatId, @NotNull Long userId) {
        return advertisementRepository.findAllByChatIdAndUserId(chatId, userId).stream()
                .map(AdvertisementMapper::getAdvertisementDTO)
                .toList();
    }

    /**
     * Сохраняет созданное пользователем объявление
     * @param advertisementDTO созданное пользователем объявление
     */
    public void saveNewAdvertisement(@NotNull AdvertisementDTO advertisementDTO) {
        advertisementRepository.save(AdvertisementMapper.getAdvertisementEntity(advertisementDTO));
    }

    /**
     * Удаляет объявление пользователя
     * @param chatId id чата
     * @param messageId id сообщения в рамках чата
     */
    public void removeAdvertisement(@NotNull Long chatId, @NotNull Long messageId) {
        advertisementRepository.deleteById(new AdvertisementEntityId(chatId, messageId));
    }

    /**
     * Получает все объявления пользователей, которые помечены к удалению
     * и требуют решения пользователя по их удалению или продлению актуальности
     * @param timestamp метка времени от 01.01.1970 в мс, начиная с которой объявление считается помеченным к удалению
     * @return список помеченных к удалению объявлений
     */
    public List<AdvertisementEntity> getMarkedForDeleteAdvertisements(@NotNull Long timestamp) {
        return advertisementRepository.findAllByNextUpdateTimeLessThanEqual(timestamp);
    }

    /**
     * Находит все объявления, содержащие все теги из списка
     * @param tags список тегов, которые должны присутствовать в объявлении
     * @return список объявлений, содержащих указанные теги
     */
    public List<AdvertisementEntity> searchAdvertisementsWithTags(@NotNull List<TagDTO> tags) {
        return advertisementRepository.findAllByTagsIn(TagMapper.getTagEntityList(tags), searchAdvertisementsLimit);
    }

}
