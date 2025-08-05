package baraholkateam.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@AllArgsConstructor
public enum Tag implements Serializable {

    // City
    Moscow("#Москва"),
    SPB("#СПб"),
    Ekaterinburg("#Екатеринбург"),
    Chelyabinsk("#Челябинск"),
    Ulyanovsk("#Ульяновск"),
    Omsk("#Омск"),
    Belgorod("#Белгород"),
    Petropavlovsk("#Петропавловск"),
    Perm("#Пермь"),
    Volgograd("#Волгоград"),
    Kirov("#Киров"),
    Khabarovsk("#Хабаровск"),
    OtherCity("#другой_город"),

    // Actuality
    Actual("#актуально"),

    // AdvertisementType
    Sale("#продажа"),
    Exchange("#обмен"),
    Gift("#дар"),
    Bargaining("#торг_уместен"),
    Urgently("#срочно"),

    // Product categories
    Clothes("#одежда"),
    Shoes("#обувь"),
    ChildrenProducts("#детские_товары"),
    BeautyAndHealth("#красота_и_здоровье"),
    Books("#книги"),
    Hobby("#хобби"),
    HomeAppliance("#домашняя_техника"),
    Electronics("#электроника"),
    Sport("#спорт"),
    Other("#другое"),
    MenGoods("#мужское"),
    WomenGoods("#женское"),

    // Unknown
    Unknown("#unknown");

    @JsonProperty("name")
    private final String name;

    /**
     * Возвращает тег по названию тега
     * @param name название тега
     * @return тег
     */
    public static Tag getTagByName(String name) {
        for (Tag tag : Tag.values()) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        log.error(Configuration.ErrorMessage.NO_TAG_FOUND, name);
        return Unknown;
    }

}
