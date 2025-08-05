package baraholkateam.command;

import baraholkateam.exception.ExceptionHelper;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import baraholkateam.util.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class NewAdvertisementConfirmCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;

    public NewAdvertisementConfirmCommand() {
        super(Command.NewAdvertisement_Confirm.getName(), Command.NewAdvertisement_Confirm.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chat.getId(), user.getId());

        String text = advertisementDTO.getAdvertisementText();

        List<PhotoDTO> photos = advertisementDTO.getPhotos();

        if (advertisementDTO.getContacts() != null && !advertisementDTO.getContacts().isEmpty()) {
            sendAnswer(
                    telegramClient,
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
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.FORMED_ADVERTISEMENT,
                true
        );

        if (photos.size() == 1) {
            try {
                telegramClient.execute(SendPhoto.builder()
                        .chatId(chat.getId())
                        .photo(new InputFile(Objects.requireNonNull(PhotoConverter.convertBase64StringToPhoto(photos.get(0).getPhoto()))))
                        .caption(text)
                        .parseMode(ParseMode.HTML)
                        .build());
            } catch (TelegramApiException e) {
                log.error(
                        "Невозможно отправить сообщение с фотографией в чат id = {} по причине: {}",
                        chat.getId(),
                        ExceptionHelper.getExceptionMessage(e)
                );
            }
        } else if (photos.size() > 1) {
            List<InputMediaPhoto> photoMediaList = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                File photoFile = PhotoConverter.convertBase64StringToPhoto(photos.get(i).getPhoto());
                if (photoFile != null) {
                    InputMediaPhoto mediaPhoto = InputMediaPhoto.builder()
                            .media(photoFile, photoFile.getName())
                            .parseMode(ParseMode.HTML)
                            .build();
                    if (i == 0) {
                        mediaPhoto.setCaption(text);
                    }
                    photoMediaList.add(mediaPhoto);
                }
            }
            try {
                telegramClient.execute(SendMediaGroup.builder()
                        .chatId(chat.getId())
                        .medias(photoMediaList)
                        .build());
            } catch (TelegramApiException e) {
                log.error(
                        "Невозможно отправить сообщение с несколькими фотографиями в чат id = {} по причине: {}",
                        chat.getId(),
                        ExceptionHelper.getExceptionMessage(e)
                );
            }
        }

        prepareConfirmAdvertisementButtons(
                photos.stream()
                        .map(PhotoDTO::getPhoto)
                        .toList()
        );
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.CONFIRM_AD_TEXT,
                true
        );
    }

    private void prepareConfirmAdvertisementButtons(List<String> photos) {
        InlineKeyboardButton yesButton = new InlineKeyboardButton("Да");
        String yesCallbackData = String.format("%s %s %d", Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA, "yes",
                photos.size() == 1 ? 0 : 1);
        yesButton.setCallbackData(yesCallbackData);

        InlineKeyboardButton noButton = new InlineKeyboardButton("Нет");
        String noCallbackData = String.format("%s %s", Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA, "no");
        noButton.setCallbackData(noCallbackData);

        InlineKeyboardRow keyboardFirstRow = new InlineKeyboardRow();
        keyboardFirstRow.add(yesButton);
        keyboardFirstRow.add(noButton);

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardFirstRow);

        replyKeyboard = new InlineKeyboardMarkup(keyboardRows);
    }

}
