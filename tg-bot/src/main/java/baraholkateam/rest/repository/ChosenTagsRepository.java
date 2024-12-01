package baraholkateam.rest.repository;

import baraholkateam.rest.model.ChosenTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChosenTagsRepository extends JpaRepository<ChosenTags, Long> {

}
