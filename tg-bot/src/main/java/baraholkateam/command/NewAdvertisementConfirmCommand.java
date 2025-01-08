package baraholkateam.command;

import baraholkateam.bot.BaraholkaBot;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import baraholkateam.util.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class NewAdvertisementConfirmCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private TelegramAPIRequests telegramAPIRequests;

    public NewAdvertisementConfirmCommand() {
        super(Command.NewAdvertisement_Confirm.getName(), Command.NewAdvertisement_Confirm.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chat.getId(), user.getId());

        String text = advertisementDTO.getAdvertisementText();

        List<PhotoDTO> photos = advertisementDTO.getPhotos();

        if (advertisementDTO.getContacts() != null && !advertisementDTO.getContacts().isEmpty()) {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(
                            Configuration.CommandMessage.CONTACTS_LIST_TEXT,
                            advertisementDTO.getContacts().stream()
                                    .map(ContactDTO::getContactName)
                                    .reduce("\n", String::concat)
                    )
            );
        }

        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.FORMED_ADVERTISEMENT,
                true
        );

        if (photos.size() == 1) {
            ((BaraholkaBot) absSender).sendPhotoMessage(
                    chat.getId(),
                    PhotoConverter.convertBase64StringToPhoto(photos.get(0).getPhoto()),
                    text
            );
        } else if (photos.size() > 1) {
            List<File> photoFiles = new ArrayList<>();
            for (PhotoDTO photo : photos) {
                photoFiles.add(Objects.requireNonNull(PhotoConverter.convertBase64StringToPhoto(photo.getPhoto())));
            }
            ((BaraholkaBot) absSender).sendPhotoMediaGroup(chat.getId(), photoFiles, text);
        }

        prepareConfirmAdvertisementButtons(
                photos.stream()
                        .map(PhotoDTO::getPhoto)
                        .toList()
        );
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.CONFIRM_AD_TEXT,
                true
        );
    }

    private void prepareConfirmAdvertisementButtons(List<String> photos) {
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        String yesCallbackData = String.format("%s %s %d", Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA, "yes",
                photos.size() == 1 ? 0 : 1);
        yesButton.setCallbackData(yesCallbackData);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        String noCallbackData = String.format("%s %s", Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA, "no");
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
