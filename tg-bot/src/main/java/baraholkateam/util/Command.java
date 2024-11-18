package baraholkateam.util;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

public enum Command {
    UnknownCommand("unknown", "Неизвестная команда"),
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

    @Getter
    private final String identifier;
    @Getter
    private final String description;
    private static final Map<Command, Command> NEXT_COMMAND = getNextCommand();
    private static final Map<Command, Command> PREVIOUS_COMMAND = getPreviousCommand();

    Command(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    private static Map<Command, Command> getNextCommand() {
        return Map.ofEntries(
                Map.entry(SearchAdvertisements, SearchAdvertisements_AddAdvertisementTypes),
                Map.entry(SearchAdvertisements_AddAdvertisementTypes, SearchAdvertisements_AddProductCategories),
                Map.entry(SearchAdvertisements_AddProductCategories, SearchAdvertisements_ShowFoundAdvertisements),
                Map.entry(NewAdvertisement, NewAdvertisement_AddPhotos),
                Map.entry(NewAdvertisement_AddPhotos, NewAdvertisement_ConfirmPhoto),
                Map.entry(NewAdvertisement_ConfirmPhoto, NewAdvertisement_AddDescription),
                Map.entry(NewAdvertisement_AddDescription, NewAdvertisement_AddCity),
                Map.entry(NewAdvertisement_AddCity, NewAdvertisement_AddAdvertisementTypes),
                Map.entry(NewAdvertisement_AddAdvertisementTypes, NewAdvertisement_AddCategories),
                Map.entry(NewAdvertisement_AddCategories, NewAdvertisement_AddPrice),
                Map.entry(NewAdvertisement_AddPrice, NewAdvertisement_ConfirmPrice),
                Map.entry(NewAdvertisement_ConfirmPrice, NewAdvertisement_AddContacts),
                Map.entry(NewAdvertisement_AddContacts, NewAdvertisement_AddPhone),
                Map.entry(NewAdvertisement_AddPhone, NewAdvertisement_ConfirmPhone),
                Map.entry(NewAdvertisement_ConfirmPhone, NewAdvertisement_AddSocial),
                Map.entry(NewAdvertisement_AddSocial, NewAdvertisement_Confirm)
        );
    }

    private static Map<Command, Command> getPreviousCommand() {
        return Map.ofEntries(
                Map.entry(Start, Start),
                Map.entry(Help, MainMenu),
                Map.entry(MainMenu, MainMenu),
                Map.entry(UserAdvertisements, MainMenu),
                Map.entry(NewAdvertisement, MainMenu),
                Map.entry(DeleteAdvertisement, MainMenu),
                Map.entry(NewAdvertisement_AddPhotos, NewAdvertisement),
                Map.entry(NewAdvertisement_ConfirmPhoto, NewAdvertisement_AddPhotos),
                Map.entry(NewAdvertisement_AddDescription, NewAdvertisement_AddPhotos),
                Map.entry(NewAdvertisement_AddCity, NewAdvertisement_AddDescription),
                Map.entry(NewAdvertisement_AddAdvertisementTypes, NewAdvertisement_AddCity),
                Map.entry(NewAdvertisement_AddCategories, NewAdvertisement_AddCity),
                Map.entry(NewAdvertisement_AddPrice, NewAdvertisement_AddCity),
                Map.entry(NewAdvertisement_ConfirmPrice, NewAdvertisement_AddPrice),
                Map.entry(NewAdvertisement_AddContacts, NewAdvertisement_AddPrice),
                Map.entry(NewAdvertisement_AddPhone, NewAdvertisement_AddContacts),
                Map.entry(NewAdvertisement_ConfirmPhone, NewAdvertisement_AddContacts),
                Map.entry(NewAdvertisement_AddSocial, NewAdvertisement_AddContacts),
                Map.entry(NewAdvertisement_Confirm, NewAdvertisement_AddContacts),
                Map.entry(SearchAdvertisements, MainMenu),
                Map.entry(SearchAdvertisements_AddAdvertisementTypes, SearchAdvertisements),
                Map.entry(SearchAdvertisements_AddProductCategories, SearchAdvertisements_AddAdvertisementTypes),
                Map.entry(SearchAdvertisements_ShowFoundAdvertisements, SearchAdvertisements_AddProductCategories)
        );
    }

    public static Command findCommand(String text) {
        for (Command command : Command.values()) {
            if (Objects.equals(command.getIdentifier(), text)) {
                return command;
            }
        }
        return null;
    }

    public static Command findCommandByDescription(String text) {
        for (Command command : Command.values()) {
            if (Objects.equals(command.getDescription(), text)) {
                return command;
            }
        }
        return null;
    }

    public static Command nextCommand(Command currentCommand) {
        if (currentCommand == null) {
            return null;
        }
        Command nextCommand = NEXT_COMMAND.get(currentCommand);
        return nextCommand == null ? currentCommand : nextCommand;
    }

    public static Command previousCommand(Command currentCommand) {
        if (currentCommand == null) {
            return null;
        }
        Command previousCommand = PREVIOUS_COMMAND.get(currentCommand);
        return previousCommand == null ? currentCommand : previousCommand;
    }

}
