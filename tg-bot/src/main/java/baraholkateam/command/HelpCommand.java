package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class HelpCommand extends BaraholkaBotCommand {

    private static final List<Command> AVAILABLE_COMMANDS = List.of(
            Command.MainMenu,
            Command.NewAdvertisement,
            Command.DeleteAdvertisement,
            Command.SearchAdvertisements,
            Command.UserAdvertisements
    );

    public HelpCommand() {
        super(Command.Help.getName(), Command.Help.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendAnswer(absSender, user, chat, String.format(Configuration.CommandMessage.HELP_INFO, getAvailableCommands()));
    }

    private static String getAvailableCommands() {
        StringBuilder result = new StringBuilder();
        for (Command command : AVAILABLE_COMMANDS) {
            result.append("/")
                    .append(command.getName())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        return result.toString();
    }

}
