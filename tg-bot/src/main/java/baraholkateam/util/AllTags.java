package baraholkateam.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllTags implements Serializable {

    @JsonProperty("city")
    private final List<String> city;

    @JsonProperty("actuality")
    private final List<String> actuality;

    @JsonProperty("advertisement_type")
    private final List<String> advertisementType;

    @JsonProperty("product_categories")
    private final List<String> productCategories;

    public AllTags() {
        city = getTagsList(TagType.City);
        actuality = getTagsList(TagType.Actuality);
        advertisementType = getTagsList(TagType.AdvertisementType);
        productCategories = getTagsList(TagType.ProductCategories);
    }

    private List<String> getTagsList(TagType tagType) {
        return new ArrayList<>(Arrays.stream(Tag.values())
                .filter(tag -> tag.getTagType() == tagType)
                .map(Tag::getName)
                .toList());
    }

}
