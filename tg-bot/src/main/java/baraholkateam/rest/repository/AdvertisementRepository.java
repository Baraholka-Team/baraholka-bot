package baraholkateam.rest.repository;

import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.model.AdvertisementEntityId;
import baraholkateam.rest.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с объявлениями пользователя
 */
@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, AdvertisementEntityId> {

    List<AdvertisementEntity> findAllByNextUpdateTimeLessThanEqual(Long currentTime);

    List<AdvertisementEntity> findAllByChatIdAndUserId(Long chatId, Long userId);

    @NativeQuery(value = "SELECT * FROM advertisement WHERE ?1 <@ tags ORDER BY creation_time DESC LIMIT ?2")
    List<AdvertisementEntity> findAllByTagsIn(List<TagEntity> tags, Integer limit);

}
