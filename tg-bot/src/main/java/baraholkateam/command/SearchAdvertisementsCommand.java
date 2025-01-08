package baraholkateam.command;

import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class SearchAdvertisementsCommand extends BaraholkaBotCommand {

    @Autowired
    private ChosenTagsService chosenTagsService;

    public SearchAdvertisementsCommand() {
        super(Command.SearchAdvertisements.getName(), Command.SearchAdvertisements.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] arguments) {
        chosenTagsService.removeChosenTags(chat.getId(), user.getId());

        prepareNextButton();
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.SEARCH_ADVERTISEMENTS, this.getCommandIdentifier()),
                true
        );

        prepareTags(TagType.City, false);
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.CHOOSE_CITY, Configuration.Buttons.NEXT_BUTTON),
                true
        );
    }

}
