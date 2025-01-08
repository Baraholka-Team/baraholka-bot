package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Последнее сообщение, отправленное ботом пользователю
 */
@Getter
@Setter
@Entity(name = "last_sent_message")
@Table(name = "last_sent_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastSentMessageEntity {

    @EmbeddedId
    private LastSentMessageEntityId lastSentMessageEntityId;

    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "message_text", length = 1024, nullable = false)
    private String messageText;

    @Column(name = "has_reply_markup", nullable = false)
    private Boolean hasReplyMarkup;

}
