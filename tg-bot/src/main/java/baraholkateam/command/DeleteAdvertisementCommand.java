package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
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
import java.util.List;

@Component
public class DeleteAdvertisementCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;

    public DeleteAdvertisementCommand() {
        super(Command.DeleteAdvertisement.getName(), Command.DeleteAdvertisement.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();
        List<AdvertisementDTO> ads = advertisementService.getUserAdvertisements(chatId, user.getId());

        if (ads == null || ads.isEmpty()) {
            sendAnswer(absSender, user, chat, Configuration.CommandMessage.NO_ADS_TO_DELETE);
        } else {
            prepareInlineKeyBoardMessage(ads);
            sendAnswer(absSender, user, chat, Configuration.CommandMessage.USER_ACTUAL_ADS_TEXT, true);
        }
    }

    private void prepareInlineKeyBoardMessage(List<AdvertisementDTO> advertisementEntities) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        advertisementEntities.forEach(advertisement -> {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            String description = advertisement.getAdvertisementText();
            int descIndex = description.indexOf(Configuration.AdvertisementDescriptionParts.DESCRIPTION_TEXT);
            inlineKeyboardButton.setText(
                    description
                            .substring(descIndex + Configuration.AdvertisementDescriptionParts.DESCRIPTION_TEXT.length(),
                                    descIndex + Configuration.AdvertisementDescriptionParts.DESCRIPTION_TEXT.length() + 40)
                            .concat("...")
            );
            inlineKeyboardButton.setCallbackData(String.format("%s %d", Configuration.CommandMessage.DELETE_CALLBACK_TEXT, advertisement.getMessageId()));
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        });

        inlineKeyboardMarkup.setKeyboard(rowList);

        replyKeyboard = inlineKeyboardMarkup;
    }

}
