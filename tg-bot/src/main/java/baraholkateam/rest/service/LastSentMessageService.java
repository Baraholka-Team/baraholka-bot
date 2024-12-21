package baraholkateam.rest.service;

import baraholkateam.rest.model.LastSentMessageEntity;
import baraholkateam.rest.repository.LastSentMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

/**
 * Сервис взаимодействия с сущностью "LastSentMessage".
 */
@Slf4j
@Service
public class LastSentMessageService {

    @Autowired
    private LastSentMessageRepository lastSentMessageRepository;

    public Message get(Long chatId) {
        Optional<LastSentMessageEntity> lastSentMessageOptional = lastSentMessageRepository.findById(chatId);
        if (lastSentMessageOptional.isPresent()) {
            return lastSentMessageOptional.get().getMessage();
        } else {
            log.error("Last sent message for chat {} not found!", chatId);
            return null;
        }
    }

    public void put(Long chatId, Message message) {
        lastSentMessageRepository.save(new LastSentMessageEntity(chatId, message));
    }

}
