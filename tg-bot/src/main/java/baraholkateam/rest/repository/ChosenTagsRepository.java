package baraholkateam.rest.repository;

import baraholkateam.rest.model.ChosenTagsEntity;
import baraholkateam.rest.model.ChosenTagsEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с выбранными пользователем тегами во время поиска объявлений по тегам
 */
@Repository
public interface ChosenTagsRepository extends JpaRepository<ChosenTagsEntity, ChosenTagsEntityId> {

}
