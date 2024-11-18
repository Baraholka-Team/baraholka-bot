package baraholkateam.command;

import baraholkateam.rest.model.CurrentAdvertisement;
import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.rest.service.PreviousStateService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NewAdvertisementConfirmPhoneCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;
    @Autowired
    private PreviousStateService previousStateService;

    public NewAdvertisementConfirmPhoneCommand() {
        super(Command.NewAdvertisement_ConfirmPhone.getIdentifier(),
                Command.NewAdvertisement_ConfirmPhone.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        CurrentAdvertisement currentAdvertisement = currentAdvertisementService.get(chat.getId());
        String phone = currentAdvertisement.getPhone();
        List<String> socials = currentAdvertisement.getContacts();
        if (phone != null && previousStateService.get(chat.getId()) != Command.NewAdvertisement_AddSocial) {
            prepareReplyKeyboard(Collections.emptyList(), true);
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.PHONE_TEXT, phone),
                    true
            );
        } else if (!socials.isEmpty()) {
            prepareReplyKeyboard(List.of(Configuration.CommandMessage.DELETE_ALL_SOCIALS), true);
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.SOCIAL_TEXT, socials.get(socials.size() - 1)),
                    true
            );
        }

        prepareAddSocial();
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.CONFIRM_PHONE_TEXT,
                true
        );
    }

    private void prepareAddSocial() {
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        String yesCallbackData = String.format("%s %s", Configuration.CommandMessage.SOCIAL_CALLBACK_DATA, "yes");
        yesButton.setCallbackData(yesCallbackData);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        String noCallbackData = String.format("%s %s", Configuration.CommandMessage.SOCIAL_CALLBACK_DATA, "no");
        noButton.setCallbackData(noCallbackData);

        List<InlineKeyboardButton> keyboardFirstRow = new ArrayList<>();
        keyboardFirstRow.add(yesButton);
        keyboardFirstRow.add(noButton);

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardFirstRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        replyKeyboard = inlineKeyboardMarkup;
    }

}
