package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;

@Component
public class NewAdvertisementAddSocialCommand extends BaraholkaBotCommand {

    public NewAdvertisementAddSocialCommand() {
        super(Command.NewAdvertisement_AddSocial.getIdentifier(), Command.NewAdvertisement_AddSocial.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_SOCIAL_TEXT,
                true
        );
    }

}
