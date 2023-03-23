package baraholkateam.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collection;

public class HelpCommand extends Command {
    private static final String HELP_INFO = """
            Полный список команд:
            %s""";
    private final Collection<IBotCommand> commands;

    public HelpCommand(String commandIdentifier, String description, Collection<IBotCommand> commands) {
        super(commandIdentifier, description);
        this.commands = commands;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                String.format(HELP_INFO, allCommands()));
    }

    private String allCommands() {
        StringBuilder result = new StringBuilder();
        for (IBotCommand command : commands) {
            result
                    .append("/")
                    .append(command.getCommandIdentifier())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        return result.toString();
    }
}
