package baraholkateam.rest.dto;

import baraholkateam.util.TagType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagTypeDTO implements Serializable {

    @JsonProperty("tagTypeId")
    private Long tagTypeId;

    @JsonProperty("tagTypeName")
    private TagType tagTypeName;

}
