package baraholkateam.rest.service;

import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.mapper.PhotoMapper;
import baraholkateam.rest.repository.PhotoRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с фотографиями товаров пользователя из объявления
 */
@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    /**
     * Получает фотографию товара пользователя из объявления по id
     * @param photoId id фотографии
     * @return фотографию пользователя или null, если такой фотографии не существует
     */
    public PhotoDTO getPhoto(@NotNull Long photoId) {
        return photoRepository.findById(photoId)
                .map(PhotoMapper::getPhotoDTO)
                .orElse(null);
    }

    /**
     * Сохраняет новую фотографию товара пользователя
     * @param photoDTO фотография товара пользователя
     */
    public void addPhoto(@NotNull PhotoDTO photoDTO) {
        photoRepository.save(PhotoMapper.getPhotoEntity(photoDTO));
    }

    /**
     * Обновляет фотографию товара пользователя
     * @param photoId id фотографии
     * @param photo новая фотография
     */
    public void updatePhoto(@NotNull Long photoId, @NotNull String photo) {
        photoRepository.findById(photoId)
                .ifPresent(photoEntity -> {
                    PhotoDTO photoDTO = PhotoMapper.getPhotoDTO(photoEntity);
                    photoDTO.setPhoto(photo);
                    photoRepository.save(PhotoMapper.getPhotoEntity(photoDTO));
                });
    }

    /**
     * Удаляет фотографию товара пользователя по id
     * @param photoId id фотографии
     */
    public void deletePhoto(@NotNull Long photoId) {
        photoRepository.deleteById(photoId);
    }

}
