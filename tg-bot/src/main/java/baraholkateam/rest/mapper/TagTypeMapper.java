package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.TagTypeDTO;
import baraholkateam.rest.model.TagTypeEntity;
import baraholkateam.util.TagType;

/**
 * Маппер бизнес сущности типов тегов и JPA сущности
 */
public class TagTypeMapper {

    public static TagTypeEntity getTagTypeEntity(TagTypeDTO tagTypeDTO) {
        return TagTypeEntity.builder()
                .tagTypeId(tagTypeDTO.getTagTypeId())
                .tagTypeName(tagTypeDTO.getTagTypeName().getName())
                .build();
    }

    public static TagTypeDTO getTagTypeDTO(TagTypeEntity tagTypeEntity) {
        return TagTypeDTO.builder()
                .tagTypeId(tagTypeEntity.getTagTypeId())
                .tagTypeName(TagType.valueOf(tagTypeEntity.getTagTypeName()))
                .build();
    }

}
