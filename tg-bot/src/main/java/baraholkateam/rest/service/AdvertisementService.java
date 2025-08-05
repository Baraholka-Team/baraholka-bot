package baraholkateam.rest.service;

import baraholkateam.configuration.AdvertisementConfiguration;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.mapper.AdvertisementMapper;
import baraholkateam.rest.mapper.TagMapper;
import baraholkateam.rest.repository.AdvertisementRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Сервис взаимодействия с объявлениями пользователей
 */
@Service
@AllArgsConstructor
public class AdvertisementService {

    private AdvertisementRepository advertisementRepository;
    private AdvertisementConfiguration advertisementConfiguration;

    /**
     * Получает все объявления пользователя из чата
     * @param chatId id чата
     * @param userId id пользователя
     * @return все объявления пользователя
     */
    @Transactional
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
    @Transactional
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
    @Transactional
    public List<AdvertisementDTO> getMarkedForDeleteAdvertisements(@NotNull Long timestamp) {
        return advertisementRepository.findAllByNextUpdateTimeLessThanEqual(timestamp).stream()
                .map(AdvertisementMapper::getAdvertisementDTO)
                .toList();
    }

    /**
     * Находит все объявления, содержащие все теги из списка
     * @param tags список тегов, которые должны присутствовать в объявлении
     * @return список объявлений, содержащих указанные теги
     */
    @Transactional
    public List<AdvertisementDTO> searchAdvertisementsWithTags(@NotNull List<TagDTO> tags) {
        return advertisementRepository.findAllByTagsIn(TagMapper.getTagEntityList(tags), advertisementConfiguration.getSearchAdvertisementLimit()).stream()
                .map(AdvertisementMapper::getAdvertisementDTO)
                .toList();
    }

}
