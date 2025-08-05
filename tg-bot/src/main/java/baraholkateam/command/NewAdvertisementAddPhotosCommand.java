package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NewAdvertisementAddPhotosCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private TelegramAPIRequests telegramAPIRequests;

    public NewAdvertisementAddPhotosCommand() {
        super(Command.NewAdvertisement_AddPhotos.getName(), Command.NewAdvertisement_AddPhotos.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_PHOTOS_TEXT,
                true
        );
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
                photoFile = telegramClient.downloadFile(telegramAPIRequests.getFilePath(photo.last().getFileId()));
            } catch (TelegramApiException e) {
                log.error(
                        "Невозможно скачать файл по пути %s".formatted(photo.last().getFilePath()),
                        e
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
}
