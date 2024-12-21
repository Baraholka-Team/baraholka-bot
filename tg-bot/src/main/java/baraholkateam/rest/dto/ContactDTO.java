package baraholkateam.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Бизнес сущность контактов пользователя из объявления
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDTO implements Serializable {

    @JsonProperty("contactId")
    private Long contactId;
    @JsonProperty("contactType")
    private ContactTypeDTO contactType;
    @JsonProperty("contactName")
    private String contactName;

}
