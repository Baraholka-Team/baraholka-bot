package baraholkateam.rest.repository;

import baraholkateam.rest.model.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {

}
