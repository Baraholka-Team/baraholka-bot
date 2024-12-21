package baraholkateam.rest.repository;

import baraholkateam.rest.model.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с контактами пользователя
 */
@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {

}
