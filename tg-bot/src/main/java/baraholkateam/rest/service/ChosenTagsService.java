package baraholkateam.rest.service;

import baraholkateam.rest.dto.ChosenTagsDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.mapper.ChosenTagsMapper;
import baraholkateam.rest.model.ChosenTagsEntityId;
import baraholkateam.rest.repository.ChosenTagsRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис взаимодействия с выбранными пользователем тегами во время поиска объявлений по тегам
 */
@Service
public class ChosenTagsService {

    @Autowired
    private ChosenTagsRepository chosenTagsRepository;

    /**
     * Получает теги, выбранные пользователем для поиска объявлений по тегам
     * @param chatId id чата
     * @param messageId id сообщения
     * @return выбранные пользователем теги или null, если сообщение в чате не найдено
     */
    public ChosenTagsDTO getChosenTags(@NotNull Long chatId, @NotNull Long messageId) {
        return chosenTagsRepository.findById(new ChosenTagsEntityId(chatId, messageId))
                .map(ChosenTagsMapper::getChosenTagsDTO)
                .orElse(null);
    }

    /**
     * Добавляет список тегов к уже имеющимся тегам
     * @param chatId id чата
     * @param messageId id сообщения
     * @param tags список тегов, добавляемый к уже заданным для поиска тегам
     */
    public void addChosenTags(@NotNull Long chatId, @NotNull Long messageId, @NotNull List<TagDTO> tags) {
        ChosenTagsDTO chosenTags = chosenTagsRepository.findById(new ChosenTagsEntityId(chatId, messageId))
                .map(ChosenTagsMapper::getChosenTagsDTO)
                .orElse(null);
        if (chosenTags != null) {
            chosenTags.addTags(tags);
        } else {
            chosenTags = ChosenTagsDTO.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .tags(tags)
                    .build();
        }
        chosenTagsRepository.save(ChosenTagsMapper.getChosenTagsEntity(chosenTags));
    }

    /**
     * Удаляет выбранные пользователем теги
     * @param chatId id чата
     * @param messageId id сообщения
     */
    public void removeChosenTags(@NotNull Long chatId, @NotNull Long messageId) {
        chosenTagsRepository.deleteById(new ChosenTagsEntityId(chatId, messageId));
    }

}
