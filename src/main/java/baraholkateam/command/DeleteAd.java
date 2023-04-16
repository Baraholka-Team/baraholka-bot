package baraholkateam.command;


import baraholkateam.database.SQLExecutor;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class DeleteAd extends Command {
    private static final String DELETE_AD = """
            Удалить выбранное объявление?""";
    private static final String NO_ADS_TO_DELETE = """
            У вас нет активных объявлений""";
    private final SQLExecutor sqlExecutor;
    public DeleteAd(String commandIdentifier, String description, SQLExecutor sqlExecutor, Map<Long, Message> lastSentMessage) {
        super(commandIdentifier, description, lastSentMessage);
        this.sqlExecutor = sqlExecutor;
    }
// TODO убрать хардкодный chatID
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long chatId = 3;
        List<Long> ads = sqlExecutor.userAds(chatId);
        if (ads.isEmpty()) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                    String.format(NO_ADS_TO_DELETE), null);
        }
        else {
            System.out.println(ads);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                    String.format(DELETE_AD), sendInlineKeyBoardMessage(ads));
        }
    }
    public InlineKeyboardMarkup sendInlineKeyBoardMessage(List<Long> ads) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        // TODO Убрать заглушку, раскомментировать основной функионал
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("3");
        inlineKeyboardButton.setCallbackData("delete 3");
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(inlineKeyboardButton);
        rowList.add(keyboardButtonsRow);
//        ads.forEach(ad -> {
//            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//            inlineKeyboardButton.setText(ad.toString());
//            inlineKeyboardButton.setCallbackData("delete " + ad);
//            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
//            keyboardButtonsRow.add(inlineKeyboardButton);
//            rowList.add(keyboardButtonsRow);
//        });
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}