package baraholkateam.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@AllArgsConstructor
public enum TagType implements Serializable {

    City("city"),
    Actuality("actuality"),
    AdvertisementType("advertisement_type"),
    ProductCategories("product_categories"),
    Unknown("unknown");

    @JsonProperty("name")
    private final String name;

    /**
     * Возвращает тип тегов по названию типа тега
     * @param name название типа тега
     * @return тип тега
     */
    public static TagType getTagTypeByName(String name) {
        for (TagType tagType : TagType.values()) {
            if (tagType.getName().equals(name)) {
                return tagType;
            }
        }
        log.error(Configuration.ErrorMessage.NO_TAG_TYPE_FOUND, name);
        return Unknown;
    }

}
