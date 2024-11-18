package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;

@Component
public class NewAdvertisementAddPhoneCommand extends BaraholkaBotCommand {

    public NewAdvertisementAddPhoneCommand() {
        super(Command.NewAdvertisement_AddPhone.getIdentifier(), Command.NewAdvertisement_AddPhone.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_PHONE_TEXT,
                true
        );
    }

}
