package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.ChosenTagsDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.model.ChosenTagsEntity;
import baraholkateam.rest.model.ChosenTagsEntityId;
import baraholkateam.rest.model.TagEntity;

import java.util.List;

/**
 * Маппер бизнес сущности выбранных пользователем тегов при поиске объявлений по тегам и JPA сущности
 */
public class ChosenTagsMapper {

    public static ChosenTagsEntity getChosenTagsEntity(ChosenTagsDTO chosenTagsDTO) {
        ChosenTagsEntityId chosenTagsEntityId = ChosenTagsEntityId.builder()
                .chatId(chosenTagsDTO.getChatId())
                .userId(chosenTagsDTO.getUserId())
                .build();

        List<TagEntity> tags = TagMapper.getTagEntityList(chosenTagsDTO.getTags());
        return ChosenTagsEntity.builder()
                .chosenTagsEntityId(chosenTagsEntityId)
                .tags(tags)
                .build();
    }

    public static ChosenTagsDTO getChosenTagsDTO(ChosenTagsEntity chosenTagsEntity) {
        List<TagDTO> tags = TagMapper.getTagDTOList(chosenTagsEntity.getTags());
        return ChosenTagsDTO.builder()
                .chatId(chosenTagsEntity.getChosenTagsEntityId().getChatId())
                .userId(chosenTagsEntity.getChosenTagsEntityId().getUserId())
                .tags(tags)
                .build();
    }

}
