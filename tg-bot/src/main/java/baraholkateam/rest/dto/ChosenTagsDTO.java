package baraholkateam.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Бизнес сущность выбранных пользователем тегов во время поиска объявлений по тегам
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChosenTagsDTO implements Serializable {

    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("tags")
    private List<TagDTO> tags = new ArrayList<>();

    public void addTag(TagDTO tag) {
        this.tags.add(tag);
    }

    public void addTags(List<TagDTO> tags) {
        this.tags.addAll(tags);
    }

}
