package baraholkateam.rest.dto;

import baraholkateam.util.ContactType;
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
public class ContactTypeDTO implements Serializable {

    @JsonProperty("contactTypeId")
    private Long contactTypeId;

    @JsonProperty("contactTypeName")
    private ContactType contactTypeName;

}
