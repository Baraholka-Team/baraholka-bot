package baraholkateam.rest.repository;

import baraholkateam.rest.model.TagEntity;
import baraholkateam.rest.model.TagTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с тегами в объявлении
 */
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    Optional<TagEntity> findByName(String name);

    List<TagEntity> findAllByTagType(TagTypeEntity tagType);

}
