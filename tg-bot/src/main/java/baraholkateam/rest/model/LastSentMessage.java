package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Последнее сообщение, отправленное ботом пользователю
 */
@Slf4j
@Getter
@Setter
@Entity
@Table(name = "last_sent_message")
@NoArgsConstructor
public class LastSentMessage {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "message")
    private Message message;

    public LastSentMessage(Long chatId, Message message) {
        this.chatId = chatId;
        this.message = message;
    }

}
