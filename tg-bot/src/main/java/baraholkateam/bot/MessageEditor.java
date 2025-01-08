package baraholkateam.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface MessageEditor {

    void editAdvertisementText(Long chatId, Integer messageId, String text);

    void editMessageReplyMarkup(Long chatId, List<List<InlineKeyboardButton>> buttons);

    void deleteLastMessage(Long chatId, Long userId);

}
