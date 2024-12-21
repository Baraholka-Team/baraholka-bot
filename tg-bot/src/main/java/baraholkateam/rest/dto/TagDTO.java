package baraholkateam.rest.dto;

import baraholkateam.util.Tag;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Бизнес сущность тегов для объявления
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO implements Serializable {

    @JsonProperty("tagId")
    private Long tagId;
    @JsonProperty("tagType")
    private TagTypeDTO tagType;
    @JsonProperty("tagName")
    private Tag tag;

}
