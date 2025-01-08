package baraholkateam.command;

import baraholkateam.bot.BaraholkaBot;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.LastSentMessageDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.LastSentMessageService;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class NewAdvertisementConfirmPhotoCommand extends BaraholkaBotCommand {

    @Autowired
    private LastSentMessageService lastSentMessageService;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private TelegramAPIRequests telegramAPIRequests;

    public NewAdvertisementConfirmPhotoCommand() {
        super(Command.NewAdvertisement_ConfirmPhoto.getName(), Command.NewAdvertisement_ConfirmPhoto.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        AdvertisementDTO advertisementDTO = advertisementService.getUserAdvertisements(chat.getId(), user.getId()).stream()
                .sorted()
                .findFirst()
                .orElse(null);

        if (advertisementDTO == null) {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    Configuration.CommandMessage.NO_USER_ADVERTISEMENTS_FOUND.formatted(user.getId())
            );
            return;
        }

        LastSentMessageDTO lastSentMessage = lastSentMessageService.getLastSentMessage(chat.getId(), user.getId());
        if (lastSentMessage.getMessage().getText().substring(0, 20).equals(Configuration.CommandMessage.CONFIRM_PHOTOS_TEXT.substring(0, 20))) {
            ((BaraholkaBot) absSender).deleteLastMessage(chat.getId(), user.getId());
        }

        int addedPhotosCount = advertisementDTO.getPhotos().size();
        if (addedPhotosCount < 11) {
            prepareAddReplyKeyboard(true);
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CONFIRM_PHOTOS_TEXT, addedPhotosCount),
                    true
            );
        } else {
            prepareAddReplyKeyboard(false);
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.NO_MORE_CONFIRM_PHOTOS_TEXT, addedPhotosCount),
                    true
            );
        }
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

    private void prepareAddReplyKeyboard(boolean ifAddPhotos) {
        if (ifAddPhotos) {
            replyKeyboard = prepareReplyKeyboard(
                    List.of(
                            Command.NewAdvertisement_AddPhotos.getDescription(),
                            Command.NewAdvertisement_AddDescription.getDescription(),
                            Configuration.CommandMessage.DELETE_ALL_PHOTOS
                    ),
                    true
            );
            return;
        }

        replyKeyboard = prepareReplyKeyboard(
                List.of(
                        Command.NewAdvertisement_AddDescription.getDescription(),
                        Configuration.CommandMessage.DELETE_ALL_PHOTOS
                ),
                true
        );
    }

}
