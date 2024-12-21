package baraholkateam.rest.repository;

import baraholkateam.rest.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с тегами в объявлении
 */
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

}
