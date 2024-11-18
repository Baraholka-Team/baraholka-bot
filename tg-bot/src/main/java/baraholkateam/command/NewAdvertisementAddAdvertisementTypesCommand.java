package baraholkateam.command;

import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class NewAdvertisementAddAdvertisementTypesCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;

    public NewAdvertisementAddAdvertisementTypesCommand() {
        super(Command.NewAdvertisement_AddAdvertisementTypes.getIdentifier(),
                Command.NewAdvertisement_AddAdvertisementTypes.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareNextButton();
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.CHOSEN_CITY, currentAdvertisementService.get(chat.getId()).getTagsOfType(TagType.City)),
                true
        );

        prepareTags(TagType.AdvertisementType, true);
        sendAnswer(absSender, user, chat, Configuration.CommandMessage.ADD_TYPE_TEXT, true);
    }

}
