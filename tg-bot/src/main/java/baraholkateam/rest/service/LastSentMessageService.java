package baraholkateam.rest.service;

import baraholkateam.rest.dto.LastSentMessageDTO;
import baraholkateam.rest.mapper.LastSentMessageMapper;
import baraholkateam.rest.model.LastSentMessageEntityId;
import baraholkateam.rest.repository.LastSentMessageRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с последним сообщением, отправленным ботом пользователю
 */
@Service
public class LastSentMessageService {

    @Autowired
    private LastSentMessageRepository lastSentMessageRepository;

    /**
     * Получает последнее сообщение, отправленное ботом пользователю
     * @param chatId id чата
     * @param userId id пользователя
     * @return последнее отправленное ботом сообщение или null, если такого сообщения не существует
     */
    public LastSentMessageDTO getLastSentMessage(@NotNull Long chatId, @NotNull Long userId) {
        return lastSentMessageRepository.findById(new LastSentMessageEntityId(chatId, userId))
                .map(LastSentMessageMapper::getLastSentMessageDTO)
                .orElse(null);
    }

    /**
     * Сохраняет последнее сообщение, отправленное ботом пользователю
     * @param lastSentMessageDTO сообщение
     */
    public void addLastSentMessage(@NotNull LastSentMessageDTO lastSentMessageDTO) {
        lastSentMessageRepository.save(LastSentMessageMapper.getLastSentMessageEntity(lastSentMessageDTO));
    }

    /**
     * Удаляет последнее сообщение, отправленное ботом пользователю
     * @param chatId id чата
     * @param userId id пользователя
     */
    public void deleteLastSentMessage(@NotNull Long chatId, @NotNull Long userId) {
        lastSentMessageRepository.deleteById(new LastSentMessageEntityId(chatId, userId));
    }

}
