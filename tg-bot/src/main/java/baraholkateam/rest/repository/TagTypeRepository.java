package baraholkateam.rest.repository;

import baraholkateam.rest.model.TagTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с типами тегов в объявлении
 */
@Repository
public interface TagTypeRepository extends JpaRepository<TagTypeEntity, Long> {

}
