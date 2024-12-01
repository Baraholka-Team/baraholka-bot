package baraholkateam.rest.repository;

import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.model.AdvertisementEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, AdvertisementEntityId> {

    List<AdvertisementEntity> findAllByNextUpdateTimeLessThanEqual(Long currentTime);

    List<AdvertisementEntity> findAllByOwnerChatId(Long ownerChatId);

    @Query(value = "SELECT * FROM advertisement WHERE ?1 <@ tags ORDER BY creation_time DESC LIMIT ?2", nativeQuery = true)
    List<AdvertisementEntity> findAllByTagsIn(String[] tags, Integer limit);

}
