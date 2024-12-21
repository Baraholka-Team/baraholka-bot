package baraholkateam.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Command implements Serializable {
    Start("start", "Старт"),
    Help("help", "Справочная информация по боту"),
    MainMenu("menu", "Главное меню"),
    UserAdvertisements("my_advertisements", "Созданные пользователем актуальные объявления"),
    NewAdvertisement("new_advertisement", "Создание нового объявления"),
    DeleteAdvertisement("delete_advertisement", "Удаление созданного объявления"),
    NewAdvertisement_AddPhotos("add_photos", "Добавить фотографии"),
    NewAdvertisement_ConfirmPhoto("confirm_photo", "Подтвердить фотографии"),
    NewAdvertisement_AddDescription("add_description", "Добавить описание"),
    NewAdvertisement_AddCity("add_city", "Добавить город"),
    NewAdvertisement_AddAdvertisementTypes("add_advertisement_types", "Выбрать типы объявления"),
    NewAdvertisement_AddCategories("add_categories", "Выбрать категории"),
    NewAdvertisement_AddPrice("add_price", "Добавить стоимость"),
    NewAdvertisement_ConfirmPrice("confirm_price", "Подтвердить стоимость"),
    NewAdvertisement_AddContacts("add_contacts", "Добавить контакты"),
    NewAdvertisement_AddPhone("add_phone", "Добавить номер телефона"),
    NewAdvertisement_ConfirmPhone("confirm_phone", "Подтвердить номер телефона"),
    NewAdvertisement_AddSocial("add_social", "Добавить ссылку"),
    NewAdvertisement_Confirm("confirm_ad", "Подтвердить"),
    SearchAdvertisements("search_advertisement", "Поиск объявлений по хэштегам"),
    SearchAdvertisements_AddAdvertisementTypes("search_advertisement_types",
            "Выбор типов объявления для поиска"),
    SearchAdvertisements_AddProductCategories("search_product_categories",
            "Выбор категорий товаров для поиска"),
    SearchAdvertisements_ShowFoundAdvertisements("show_found_advertisements",
            "Вывод найденных объявлений");

    @JsonProperty("name")
    private final String name;
    @JsonProperty("description") // TODO убрать description в базу
    private final String description;

}
