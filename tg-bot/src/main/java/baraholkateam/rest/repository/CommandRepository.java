package baraholkateam.rest.repository;

import baraholkateam.rest.model.CommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий команд в боте
 */
@Repository
public interface CommandRepository extends JpaRepository<CommandEntity, Long> {

}
