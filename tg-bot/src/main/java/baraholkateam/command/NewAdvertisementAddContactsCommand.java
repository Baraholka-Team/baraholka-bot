package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NewAdvertisementAddContactsCommand extends BaraholkaBotCommand {

    public NewAdvertisementAddContactsCommand() {
        super(Command.NewAdvertisement_AddContacts.getName(), Command.NewAdvertisement_AddContacts.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_CONTACTS_TEXT,
                true
        );

        prepareAddPhoneButtons();
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_CONTACTS_QUESTION,
                true
        );
    }

    private void prepareAddPhoneButtons() {
        InlineKeyboardButton yesButton = new InlineKeyboardButton("Да");
        String yesCallbackData = String.format("%s %s", Configuration.CommandMessage.PHONE_CALLBACK_DATA, "yes");
        yesButton.setCallbackData(yesCallbackData);

        InlineKeyboardButton noButton = new InlineKeyboardButton("Нет");
        String noCallbackData = String.format("%s %s", Configuration.CommandMessage.PHONE_CALLBACK_DATA, "no");
        noButton.setCallbackData(noCallbackData);

        List<InlineKeyboardButton> keyboardFirstRow = new ArrayList<>();
        keyboardFirstRow.add(yesButton);
        keyboardFirstRow.add(noButton);

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(new InlineKeyboardRow(keyboardFirstRow));

        replyKeyboard = new InlineKeyboardMarkup(keyboardRows);
    }

}
