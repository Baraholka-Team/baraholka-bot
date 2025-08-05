package baraholkateam.command;

import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class MainMenuCommand extends BaraholkaBotCommand {

    public MainMenuCommand() {
        super(Command.MainMenu.getName(), Command.MainMenu.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        prepareButtons();
        sendAnswer(
                telegramClient,
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
        prepareReplyKeyboard(List.of(
                Command.NewAdvertisement.getDescription(),
                Command.DeleteAdvertisement.getDescription(),
                Command.SearchAdvertisements.getDescription(),
                Command.UserAdvertisements.getDescription(),
                Command.Help.getDescription()
        ), false);
    }

}
