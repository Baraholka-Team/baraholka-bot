package baraholkateam.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public interface MessageEditor {

    void editAdvertisementText(Long chatId, Long userId, String text);

    void editMessageReplyMarkup(Long chatId, Long userId, List<InlineKeyboardRow> buttons);

    void deleteLastMessage(Long chatId, Long userId);

}
