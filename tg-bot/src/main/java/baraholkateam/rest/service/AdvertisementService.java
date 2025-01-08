package baraholkateam.rest.service;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.mapper.AdvertisementMapper;
import baraholkateam.rest.mapper.TagMapper;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.repository.AdvertisementRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
     * Возвращает последнее созданное пользователем объявление
     * @param chatId id чата
     * @param userId id пользователя
     * @return последнее объявление пользователя или null, если пользователь не создал ни одного объявления
     */
    public AdvertisementDTO getLastUserAdvertisement(@NotNull Long chatId, @NotNull Long userId) {
        return getUserAdvertisements(chatId, userId).stream()
                .max(Comparator.comparingLong(AdvertisementDTO::getCreationTime))
                .orElse(null);
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
    public void removeAdvertisement(@NotNull Long chatId, @NotNull Integer messageId) {
        advertisementRepository.deleteByChatIdAndMessageId(chatId, messageId);
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
