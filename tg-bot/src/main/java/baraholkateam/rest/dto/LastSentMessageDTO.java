package baraholkateam.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.Serializable;

/**
 * Бизнес сущность последнего отправленного ботом сообщения
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastSentMessageDTO implements Serializable {

    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("message")
    private Message message;

}
