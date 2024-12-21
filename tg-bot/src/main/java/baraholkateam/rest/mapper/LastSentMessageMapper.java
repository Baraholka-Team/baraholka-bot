package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.LastSentMessageDTO;
import baraholkateam.rest.model.LastSentMessageEntity;
import baraholkateam.rest.model.LastSentMessageEntityId;
import org.telegram.telegrambots.meta.api.objects.Message;

public class LastSentMessageMapper {

    public static LastSentMessageEntity getLastSentMessageEntity(LastSentMessageDTO lastSentMessageDTO) {
        LastSentMessageEntityId lastSentMessageEntityId = LastSentMessageEntityId.builder()
                .chatId(lastSentMessageDTO.getChatId())
                .userId(lastSentMessageDTO.getUserId())
                .build();

        return LastSentMessageEntity.builder()
                .lastSentMessageEntityId(lastSentMessageEntityId)
                .messageId(Long.valueOf(lastSentMessageDTO.getMessage().getMessageId()))
                .messageText(lastSentMessageDTO.getMessage().getText())
                .build();
    }

    public static LastSentMessageDTO getLastSentMessageDTO(LastSentMessageEntity lastSentMessageEntity) {
        Message message = new Message();
        message.setMessageId(lastSentMessageEntity.getMessageId().intValue());
        message.setText(lastSentMessageEntity.getMessageText());

        return LastSentMessageDTO.builder()
                .chatId(lastSentMessageEntity.getLastSentMessageEntityId().getChatId())
                .userId(lastSentMessageEntity.getLastSentMessageEntityId().getUserId())
                .message(message)
                .build();
    }

}
