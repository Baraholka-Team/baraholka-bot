package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.mapper.AdvertisementMapper;
import baraholkateam.rest.model.AdvertisementEntity;
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
        super(Command.DeleteAdvertisement.getIdentifier(), Command.DeleteAdvertisement.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();
        List<AdvertisementEntity> ads = advertisementService.getByChatId(chatId);

        if (ads == null || ads.isEmpty()) {
            sendAnswer(absSender, user, chat, Configuration.CommandMessage.NO_ADS_TO_DELETE);
        } else {
            prepareInlineKeyBoardMessage(ads);
            sendAnswer(absSender, user, chat, Configuration.CommandMessage.USER_ACTUAL_ADS_TEXT, true);
        }
    }

    private void prepareInlineKeyBoardMessage(List<AdvertisementEntity> advertisementEntities) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        advertisementEntities.forEach(advertisement -> {
            AdvertisementDTO advertisementDTO = AdvertisementMapper.getAdvertisementDTO(advertisement);
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            String description = advertisementDTO.getAdvertisementText();
            int descIndex = description.indexOf(Configuration.Advertisement.DESCRIPTION_TEXT);
            inlineKeyboardButton.setText(
                    description
                            .substring(descIndex + Configuration.Advertisement.DESCRIPTION_TEXT.length(),
                                    descIndex + Configuration.Advertisement.DESCRIPTION_TEXT.length() + 40)
                            .concat("...")
            );
            inlineKeyboardButton.setCallbackData(String.format("%s %d", Configuration.CommandMessage.DELETE_CALLBACK_TEXT, advertisementDTO.getMessageId()));
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        });

        inlineKeyboardMarkup.setKeyboard(rowList);

        replyKeyboard = inlineKeyboardMarkup;
    }

}
