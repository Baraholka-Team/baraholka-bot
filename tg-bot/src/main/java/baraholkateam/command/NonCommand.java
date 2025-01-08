package baraholkateam.command;

import baraholkateam.util.Command;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NonCommand {

    private static final String NO_CURRENT_STATE = """
            Ошибка в текущем состоянии бота.
            Пожалуйста, вернитесь в /%s и следуйте инструкциям.""";
    private static final String COMMAND_ERROR_MESSAGE = """
            Команда /%s не предполагает ввод сообщений.
            Пожалуйста, вернитесь в /%s и следуйте инструкциям.""";
    private static final String UNKNOWN_COMMAND = "Введеное сообщение не понятно боту.";
    private static final String CHOOSE_CITY = "Пожалуйста, выберите город для поиска.";
    private static final String CHOOSE_ADVERTISEMENT_TYPES = "Пожалуйста, выберите типы объявления.";
    private static final String CHOOSE_PRODUCT_CATEGORIES = "Пожалуйста, выберите категории объявлений.";
    private static final String EMPTY_DESCRIPTION = """
            Длина описания превышает допустимый предел в 800 символов или был произведен ввод в некорректной форме.
            Пожалуйста, введите описание еще раз.""";
    private static final String INVALID_PHONE_NUMBER = """
            Номер телефона имеет неверный формат.
            Допустимый формат телефона: +7-xxx-xxx-xx-xx
            Пожалуйста, введите номер телефона еще раз.""";
    private static final String INVALID_SOCIAL = """
            Ссылка на социальную сеть имеет неверный формат.
            Пожалуйста, введите ссылку еще раз.""";
    private static final String INVALID_PRICE = """
            Введенная цена имеет неверный формат.
            Цена должна состоять только из цифр и ее длина не должна превышать 18 символов.
            Пожалуйста, введите цену еще раз.""";

    public List<AnswerPair> nonCommandExecute(Command currentCommand) {
        if (currentCommand == null) {
            return List.of(new AnswerPair(String.format(NO_CURRENT_STATE, Command.MainMenu.getName()), true));
        }

        if (currentCommand.equals(Command.Start)
                || currentCommand.equals(Command.Help)
                || currentCommand.equals(Command.MainMenu)
                || currentCommand.equals(Command.NewAdvertisement)
        ) {
            return List.of(new AnswerPair(String.format(COMMAND_ERROR_MESSAGE, currentCommand.getName(),
                    Command.MainMenu.getName()), true));
        } else if (currentCommand.equals(Command.NewAdvertisement_AddDescription)) {
            return List.of(new AnswerPair(EMPTY_DESCRIPTION, true));
        } else if (currentCommand.equals(Command.NewAdvertisement_AddPhone)) {
           return List.of(new AnswerPair(INVALID_PHONE_NUMBER, true));
        } else if (currentCommand.equals(Command.NewAdvertisement_AddSocial)) {
            return List.of(new AnswerPair(INVALID_SOCIAL, true));
        } else if (currentCommand.equals(Command.NewAdvertisement_AddPrice)) {
            return List.of(new AnswerPair(INVALID_PRICE, true));
        } else if (currentCommand.equals(Command.SearchAdvertisements)) {
            return List.of(new AnswerPair(CHOOSE_CITY, true));
        } else if (currentCommand.equals(Command.SearchAdvertisements_AddAdvertisementTypes)) {
            return List.of(new AnswerPair(CHOOSE_ADVERTISEMENT_TYPES, true));
        } else if (currentCommand.equals(Command.SearchAdvertisements_AddProductCategories)) {
            return List.of(new AnswerPair(CHOOSE_PRODUCT_CATEGORIES, true));
        } else if (currentCommand.equals(Command.SearchAdvertisements_ShowFoundAdvertisements)) {
            return List.of(new AnswerPair(UNKNOWN_COMMAND, true));
        }
        return List.of(new AnswerPair(UNKNOWN_COMMAND, true));
    }

    /**
     * Хранит ответ бота с индикатором ранее присланного ошибочного сообщения от пользователя.
     * @param answer ответ бота
     * @param isError содержит ли предыдущий ответ пользователя ошибку
     */
    public record AnswerPair(String answer, Boolean isError) { }

}
