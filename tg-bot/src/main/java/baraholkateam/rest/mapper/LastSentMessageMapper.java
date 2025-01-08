package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.LastSentMessageDTO;
import baraholkateam.rest.model.LastSentMessageEntity;
import baraholkateam.rest.model.LastSentMessageEntityId;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Маппер бизнес сущности последнего отправленного ботом сообщения и JPA сущности
 */
public class LastSentMessageMapper {

    public static LastSentMessageEntity getLastSentMessageEntity(LastSentMessageDTO lastSentMessageDTO) {
        LastSentMessageEntityId lastSentMessageEntityId = LastSentMessageEntityId.builder()
                .chatId(lastSentMessageDTO.getChatId())
                .userId(lastSentMessageDTO.getUserId())
                .build();

        return LastSentMessageEntity.builder()
                .lastSentMessageEntityId(lastSentMessageEntityId)
                .messageId(lastSentMessageDTO.getMessage().getMessageId())
                .messageText(lastSentMessageDTO.getMessage().getText())
                .hasReplyMarkup(lastSentMessageDTO.getMessage().hasReplyMarkup())
                .build();
    }

    public static LastSentMessageDTO getLastSentMessageDTO(LastSentMessageEntity lastSentMessageEntity) {
        Message message = new Message();
        message.setMessageId(lastSentMessageEntity.getMessageId());
        message.setText(lastSentMessageEntity.getMessageText());
        if (lastSentMessageEntity.getHasReplyMarkup()) {
            message.setReplyMarkup(new InlineKeyboardMarkup());
        }

        return LastSentMessageDTO.builder()
                .chatId(lastSentMessageEntity.getLastSentMessageEntityId().getChatId())
                .userId(lastSentMessageEntity.getLastSentMessageEntityId().getUserId())
                .message(message)
                .build();
    }

}
