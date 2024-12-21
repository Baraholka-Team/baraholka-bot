package baraholkateam.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum ContactType implements Serializable {

    Phone("phone"),
    Email("email"),
    Social("social"),
    Unknown("unknown");

    @JsonProperty("name")
    private final String name;

}
