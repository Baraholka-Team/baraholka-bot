package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Сообщения, отправляемые пользователю во время уточнения актуальности уведомлений.
 */
@Setter
@Getter
@Entity
@IdClass(NotificationMessagesId.class)
@Table(name = "notification_messages")
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessages {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Id
    @Column(name = "message_id")
    private Long messageId;

    @Id
    @Column(name = "notification_message")
    private Message notificationMessage;

}
