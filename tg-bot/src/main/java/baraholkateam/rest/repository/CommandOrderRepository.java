package baraholkateam.rest.repository;

import baraholkateam.rest.model.CommandEntity;
import baraholkateam.rest.model.CommandOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий порядка команд
 */
@Repository
public interface CommandOrderRepository extends JpaRepository<CommandOrderEntity, Long> {

    Optional<CommandOrderEntity> findByCurrentCommand(CommandEntity currentCommand);

}
