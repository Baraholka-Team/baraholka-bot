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
        super(Command.MainMenu.getName(), Command.MainMenu.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareButtons();
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.MAIN_MENU,
                        Command.NewAdvertisement.getName(),
                        Command.DeleteAdvertisement.getName(),
                        Command.SearchAdvertisements.getName(),
                        Command.UserAdvertisements.getName(),
                        Command.Help.getName()
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
