package baraholkateam.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum TagType implements Serializable {

    City("city"),
    Actuality("actuality"),
    AdvertisementType("advertisement_type"),
    ProductCategories("product_categories");

    @JsonProperty("name")
    private final String name;

}
