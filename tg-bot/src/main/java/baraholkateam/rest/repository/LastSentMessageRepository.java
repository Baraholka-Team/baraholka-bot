package baraholkateam.rest.repository;

import baraholkateam.rest.model.LastSentMessageEntity;
import baraholkateam.rest.model.LastSentMessageEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с последним сообщением, отправленным ботом пользователю
 */
@Repository
public interface LastSentMessageRepository extends JpaRepository<LastSentMessageEntity, LastSentMessageEntityId> {

}
