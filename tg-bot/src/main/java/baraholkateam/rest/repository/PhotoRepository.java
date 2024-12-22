package baraholkateam.rest.repository;

import baraholkateam.rest.model.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с фотографиями товаров из объявления пользователя
 */
@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {

}
