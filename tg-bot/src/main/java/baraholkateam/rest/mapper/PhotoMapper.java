package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.model.PhotoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер бизнес сущности фотографии из объявления пользователя и JPA сущности
 */
public class PhotoMapper {
    
    public static PhotoEntity getPhotoEntity(PhotoDTO photoDTO) {
        return PhotoEntity.builder()
                .photoId(photoDTO.getPhotoId())
                .photo(photoDTO.getPhoto())
                .build();
    }
    
    public static PhotoDTO getPhotoDTO(PhotoEntity photoEntity) {
        return PhotoDTO.builder()
                .photoId(photoEntity.getPhotoId())
                .photo(photoEntity.getPhoto())
                .build();
    }
    
    public static List<PhotoEntity> getPhotoEntityList(List<PhotoDTO> photoDTOList) {
        return photoDTOList.stream()
                .map(PhotoMapper::getPhotoEntity)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<PhotoDTO> getPhotoDTOList(List<PhotoEntity> photoEntityList) {
        return photoEntityList.stream()
                .map(PhotoMapper::getPhotoDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
}
