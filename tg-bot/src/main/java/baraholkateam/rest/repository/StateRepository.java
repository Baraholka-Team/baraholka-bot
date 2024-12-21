package baraholkateam.rest.repository;

import baraholkateam.rest.model.StateEntity;
import baraholkateam.rest.model.StateEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с состоянием бота
 */
@Repository
public interface StateRepository extends JpaRepository<StateEntity, StateEntityId> {

}
