package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;

@Component
public class NewAdvertisementAddPhotosCommand extends BaraholkaBotCommand {

    public NewAdvertisementAddPhotosCommand() {
        super(Command.NewAdvertisement_AddPhotos.getIdentifier(), Command.NewAdvertisement_AddPhotos.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_PHOTOS_TEXT,
                true
        );
    }

}
