package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.dto.TagTypeDTO;
import baraholkateam.rest.model.TagEntity;
import baraholkateam.rest.model.TagTypeEntity;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;

import java.util.List;

/**
 * Маппер бизнес сущности тегов и JPA сущности
 */
public class TagMapper {

    public static TagEntity getTagEntity(TagDTO tagDTO) {
        TagTypeDTO tagTypeDTO = tagDTO.getTagType();
        TagTypeEntity tagTypeEntity = TagTypeEntity.builder()
                .tagTypeId(tagTypeDTO.getTagTypeId())
                .tagTypeName(tagTypeDTO.getTagTypeName().getName())
                .build();
        return TagEntity.builder()
                .tagId(tagDTO.getTagId())
                .tagType(tagTypeEntity)
                .name(tagDTO.getTag().getName())
                .build();
    }

    public static TagDTO getTagDTO(TagEntity tagEntity) {
        TagTypeEntity tagTypeEntity = tagEntity.getTagType();
        TagTypeDTO tagTypeDTO = TagTypeDTO.builder()
                .tagTypeId(tagTypeEntity.getTagTypeId())
                .tagTypeName(TagType.valueOf(tagTypeEntity.getTagTypeName()))
                .build();
        return TagDTO.builder()
                .tagId(tagEntity.getTagId())
                .tagType(tagTypeDTO)
                .tag(Tag.valueOf(tagEntity.getName()))
                .build();
    }

    public static List<TagEntity> getTagEntityList(List<TagDTO> tagDTOList) {
        return tagDTOList.stream()
                .map(TagMapper::getTagEntity)
                .toList();
    }

    public static List<TagDTO> getTagDTOList(List<TagEntity> tagEntityList) {
        return tagEntityList.stream()
                .map(TagMapper::getTagDTO)
                .toList();
    }

}
