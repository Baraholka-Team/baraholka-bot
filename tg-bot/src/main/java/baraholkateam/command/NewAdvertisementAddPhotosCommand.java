package baraholkateam.command;

import baraholkateam.bot.BaraholkaBot;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_PHOTOS_TEXT,
                true
        );
    }

    @Override
    public TextProcessResult processUserInput(AbsSender absSender, Message message) {
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
                        absSender,
                        message.getFrom(),
                        message.getChat(),
                        Configuration.CommandMessage.NO_MORE_PHOTOS_ADD
                );
                return;
            }
            String photoString = PhotoConverter.convertPhotoToBase64String(
                    ((BaraholkaBot) absSender).downloadFileByFilePath(
                            telegramAPIRequests.getFilePath(photo.last().getFileId())
                    )
            );
            PhotoDTO photoDTO = PhotoDTO.builder()
                    .photo(photoString)
                    .build();
            advertisementDTO.addPhoto(photoDTO);
        });

        advertisementService.saveNewAdvertisement(advertisementDTO);

        return new TextProcessResult(false, Configuration.CommandMessage.PHOTO_ADDED);
    }
}
