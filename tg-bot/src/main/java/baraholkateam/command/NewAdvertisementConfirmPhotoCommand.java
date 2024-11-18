package baraholkateam.command;

import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class NewAdvertisementConfirmPhotoCommand extends BaraholkaBotCommand {

    @Autowired
    private LastSentMessageService lastSentMessageService;
    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;

    public NewAdvertisementConfirmPhotoCommand() {
        super(Command.NewAdvertisement_ConfirmPhoto.getIdentifier(),
                Command.NewAdvertisement_ConfirmPhoto.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        int addedPhotosCount = currentAdvertisementService.getPhotos(chat.getId()).size();

        Message lastSentMessage = lastSentMessageService.get(chat.getId());
        if (lastSentMessage.getText().substring(0, 20).equals(Configuration.CommandMessage.CONFIRM_PHOTOS_TEXT.substring(0, 20))) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chat.getId());
            deleteMessage.setMessageId(lastSentMessage.getMessageId());
            try {
                absSender.execute(deleteMessage);
            } catch (TelegramApiException e) {
                log.error("Cannot delete message", e);
            }
        }

        if (addedPhotosCount < 10) {
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
