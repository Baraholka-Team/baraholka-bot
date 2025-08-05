package baraholkateam.command;

import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class SearchAdvertisementsCommand extends BaraholkaBotCommand {

    @Autowired
    private ChosenTagsService chosenTagsService;

    public SearchAdvertisementsCommand() {
        super(Command.SearchAdvertisements.getName(), Command.SearchAdvertisements.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        chosenTagsService.removeChosenTags(chat.getId(), user.getId());

        prepareNextButton();
        sendAnswer(
                telegramClient,
                user,
                chat,
                String.format(Configuration.CommandMessage.SEARCH_ADVERTISEMENTS, this.getCommandIdentifier()),
                true
        );

        prepareTags(TagType.City, false);
        sendAnswer(
                telegramClient,
                user,
                chat,
                String.format(Configuration.CommandMessage.CHOOSE_CITY, Configuration.Buttons.NEXT_BUTTON),
                true
        );
    }

}
