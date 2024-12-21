package baraholkateam.rest.service;

import baraholkateam.rest.dto.ChosenTagsDTO;
import baraholkateam.rest.mapper.ChosenTagsMapper;
import baraholkateam.rest.model.ChosenTagsEntity;
import baraholkateam.rest.model.ChosenTagsEntityId;
import baraholkateam.rest.repository.ChosenTagsRepository;
import baraholkateam.util.Tag;
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
     * Получает теги из сообщения пользователя
     * @param chatId id чата
     * @param messageId id сообщения
     * @return
     */
    public ChosenTagsDTO get(Long chatId, Long messageId) {
        return chosenTagsRepository.findById(new ChosenTagsEntityId(chatId, messageId))
                .map(ChosenTagsMapper::getChosenTagsDTO)
                .orElse(null);
    }

    public void put(Long chatId, List<Tag> tags) {
        chosenTagsRepository.save(new ChosenTagsEntity(chatId, tags));
    }

    public void delete(Long chatId) {
        chosenTagsRepository.deleteById(chatId);
    }

}
