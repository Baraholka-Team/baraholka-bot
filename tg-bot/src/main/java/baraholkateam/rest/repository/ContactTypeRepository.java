package baraholkateam.rest.repository;

import baraholkateam.rest.model.ContactTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с типами контактов пользователя
 */
@Repository
public interface ContactTypeRepository extends JpaRepository<ContactTypeEntity, Long> {

}
