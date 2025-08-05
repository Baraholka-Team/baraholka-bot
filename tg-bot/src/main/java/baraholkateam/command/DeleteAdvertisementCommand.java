package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeleteAdvertisementCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;

    public DeleteAdvertisementCommand() {
        super(Command.DeleteAdvertisement.getName(), Command.DeleteAdvertisement.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        long chatId = chat.getId();
        List<AdvertisementDTO> ads = advertisementService.getUserAdvertisements(chatId, user.getId());

        if (ads == null || ads.isEmpty()) {
            sendAnswer(telegramClient, user, chat, Configuration.CommandMessage.NO_ADS_TO_DELETE);
        } else {
            prepareInlineKeyBoardMessage(ads);
            sendAnswer(telegramClient, user, chat, Configuration.CommandMessage.USER_ACTUAL_ADS_TEXT, true);
        }
    }

    private void prepareInlineKeyBoardMessage(List<AdvertisementDTO> advertisementEntities) {
        List<InlineKeyboardRow> rowList = new ArrayList<>();

        advertisementEntities.forEach(advertisement -> {
            String description = advertisement.getAdvertisementText();
            InlineKeyboardButton inlineKeyboardButton = getInlineKeyboardButton(description);
            inlineKeyboardButton.setCallbackData(String.format("%s %d", Configuration.CommandMessage.DELETE_CALLBACK_TEXT, advertisement.getMessageId()));
            InlineKeyboardRow keyboardButtonsRow = new InlineKeyboardRow(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        });

        replyKeyboard = new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getInlineKeyboardButton(String description) {
        int descIndex = description.indexOf(Configuration.AdvertisementDescriptionParts.DESCRIPTION_TEXT);
        return new InlineKeyboardButton(
                description
                        .substring(descIndex + Configuration.AdvertisementDescriptionParts.DESCRIPTION_TEXT.length(),
                                descIndex + Configuration.AdvertisementDescriptionParts.DESCRIPTION_TEXT.length() + 40)
                        .concat("..."));
    }

}
