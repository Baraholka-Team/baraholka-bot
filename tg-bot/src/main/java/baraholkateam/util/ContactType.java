package baraholkateam.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactType {

    Phone("phone"),
    Email("email"),
    Social("social"),
    Unknown("unknown");

    private final String name;

}
