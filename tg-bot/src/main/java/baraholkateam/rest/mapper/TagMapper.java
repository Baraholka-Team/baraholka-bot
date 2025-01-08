package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.model.TagEntity;
import baraholkateam.util.Tag;

import java.util.List;

/**
 * Маппер бизнес сущности тегов и JPA сущности
 */
public class TagMapper {

    public static TagEntity getTagEntity(TagDTO tagDTO) {
        return TagEntity.builder()
                .tagId(tagDTO.getTagId())
                .tagType(TagTypeMapper.getTagTypeEntity(tagDTO.getTagType()))
                .name(tagDTO.getTag().getName())
                .build();
    }

    public static TagDTO getTagDTO(TagEntity tagEntity) {
        return TagDTO.builder()
                .tagId(tagEntity.getTagId())
                .tagType(TagTypeMapper.getTagTypeDTO(tagEntity.getTagType()))
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
