package baraholkateam.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagType {

    City("city"),
    Actuality("actuality"),
    AdvertisementType("advertisement_type"),
    ProductCategories("product_categories");

    private final String name;

}
