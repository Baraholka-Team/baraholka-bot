package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class StartCommand extends BaraholkaBotCommand {

    public StartCommand() {
        super(Command.Start.getName(), Command.Start.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] arguments) {
        prepareReplyKeyboard(
                List.of(
                        Command.NewAdvertisement.getDescription(),
                        Command.SearchAdvertisements.getDescription(),
                        Command.UserAdvertisements.getDescription(),
                        Command.MainMenu.getDescription(),
                        Command.Help.getDescription()
                ),
                false
        );
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(
                        Configuration.CommandMessage.START_ANSWER,
                        Command.NewAdvertisement.getName(),
                        Command.SearchAdvertisements.getName(),
                        Command.UserAdvertisements.getName(),
                        Command.MainMenu.getName(),
                        Command.Help.getName()
                ),
                true
        );
    }

}
