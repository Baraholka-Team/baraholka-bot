package baraholkateam.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tag {

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
    WomenGoods("#женское");

    private final String name;

}
