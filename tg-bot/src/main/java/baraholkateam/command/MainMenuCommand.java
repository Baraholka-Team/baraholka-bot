package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class MainMenuCommand extends BaraholkaBotCommand {

    public MainMenuCommand() {
        super(Command.MainMenu.getIdentifier(), Command.MainMenu.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareButtons();
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.MAIN_MENU,
                        Command.NewAdvertisement.getIdentifier(),
                        Command.DeleteAdvertisement.getIdentifier(),
                        Command.SearchAdvertisements.getIdentifier(),
                        Command.UserAdvertisements.getIdentifier(),
                        Command.Help.getIdentifier()
                ),
                true
        );
    }

    private void prepareButtons() {
        replyKeyboard = prepareReplyKeyboard(List.of(
                Command.NewAdvertisement.getDescription(),
                Command.DeleteAdvertisement.getDescription(),
                Command.SearchAdvertisements.getDescription(),
                Command.UserAdvertisements.getDescription(),
                Command.Help.getDescription()
        ), false);
    }

}
