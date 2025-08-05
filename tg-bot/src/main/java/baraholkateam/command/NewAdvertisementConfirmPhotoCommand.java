package baraholkateam.command;

import baraholkateam.exception.ExceptionHelper;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.LastSentMessageDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.rest.service.StateService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NewAdvertisementConfirmPhotoCommand extends BaraholkaBotCommand {

    @Autowired
    private LastSentMessageService lastSentMessageService;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private StateService stateService;

    public NewAdvertisementConfirmPhotoCommand() {
        super(Command.NewAdvertisement_ConfirmPhoto.getName(), Command.NewAdvertisement_ConfirmPhoto.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        AdvertisementDTO advertisementDTO = advertisementService.getUserAdvertisements(chat.getId(), user.getId()).stream()
                .sorted()
                .findFirst()
                .orElse(new AdvertisementDTO());

        LastSentMessageDTO lastSentMessage = lastSentMessageService.getLastSentMessage(chat.getId(), user.getId());
        if (lastSentMessage.getMessage().getText().substring(0, 20).equals(Configuration.CommandMessage.CONFIRM_PHOTOS_TEXT.substring(0, 20))) {
            try {
                telegramClient.execute(DeleteMessage.builder()
                        .chatId(chat.getId())
                        .messageId(lastSentMessage.getMessage().getMessageId())
                        .build());
            } catch (TelegramApiException e) {
                log.error(
                        "Невозможно удалить последнее сообщение с фотографией в чате id = {} по причине {}",
                        chat.getId(),
                        ExceptionHelper.getExceptionMessage(e)
                );
            }
        }

        int addedPhotosCount = advertisementDTO.getPhotos().size();
        if (addedPhotosCount < 11) {
            prepareAddReplyKeyboard(true);
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CONFIRM_PHOTOS_TEXT, addedPhotosCount),
                    true
            );
        } else {
            prepareAddReplyKeyboard(false);
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.NO_MORE_CONFIRM_PHOTOS_TEXT, addedPhotosCount),
                    true
            );
        }
    }

    @Override
    public TextProcessResult processUserInput(TelegramClient telegramClient, Message message) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
        int advertisementCurrentPhotosCount = advertisementDTO
                .getPhotos()
                .size();
        AtomicInteger canAddPhotosCount = new AtomicInteger(10 - advertisementCurrentPhotosCount);

        Map<String, TreeSet<PhotoSize>> photos = message.getPhoto().stream()
                .collect(
                        Collectors.groupingBy(
                                // FIXME мапим пока только по первой половине символов id фотографий
                                photo -> photo.getFileId().substring(0, photo.getFileId().length() / 2),
                                Collectors.toCollection(
                                        () -> new TreeSet<>(Comparator.comparingInt(PhotoSize::getWidth))
                                )
                        )
                );


        // добавляем фотографии самого высокого разрешения из числа одинаковых фотографий разного разрешения
        photos.forEach((make, photo) -> {
            if (canAddPhotosCount.getAndDecrement() <= 0) {
                sendAnswer(
                        telegramClient,
                        message.getFrom(),
                        message.getChat(),
                        Configuration.CommandMessage.NO_MORE_PHOTOS_ADD
                );
                return;
            }

            File photoFile = null;
            try {
                photoFile = telegramClient.downloadFile(photo.last().getFilePath());
            } catch (TelegramApiException e) {
                log.error(
                        "Невозможно скачать файл по пути {} по причине: {}",
                        photo.last().getFilePath(),
                        ExceptionHelper.getExceptionMessage(e)
                );
            }
            if (photoFile != null) {
                String photoString = PhotoConverter.convertPhotoToBase64String(photoFile);
                PhotoDTO photoDTO = PhotoDTO.builder()
                        .photo(photoString)
                        .build();
                advertisementDTO.addPhoto(photoDTO);
            }
        });

        advertisementService.saveNewAdvertisement(advertisementDTO);

        return new TextProcessResult(false, Configuration.CommandMessage.PHOTO_ADDED);
    }

    @Override
    public void processReplyKeyboardCommandText(TelegramClient telegramClient, Message message) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        if (message.hasText() && Objects.equals(message.getText(), Configuration.CommandMessage.DELETE_ALL_PHOTOS)) {
            AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
            advertisementDTO.setPhotos(new ArrayList<>());
            advertisementService.saveNewAdvertisement(advertisementDTO);
            sendAnswer(telegramClient, message.getFrom(), message.getChat(), Configuration.CommandMessage.PHOTOS_DELETE);
        }
    }

    private void prepareAddReplyKeyboard(boolean ifAddPhotos) {
        if (ifAddPhotos) {
            prepareReplyKeyboard(
                    List.of(
                            Command.NewAdvertisement_AddPhotos.getDescription(),
                            Command.NewAdvertisement_AddDescription.getDescription(),
                            Configuration.CommandMessage.DELETE_ALL_PHOTOS
                    ),
                    true
            );
            return;
        }

        prepareReplyKeyboard(
                List.of(
                        Command.NewAdvertisement_AddDescription.getDescription(),
                        Configuration.CommandMessage.DELETE_ALL_PHOTOS
                ),
                true
        );
    }

}
