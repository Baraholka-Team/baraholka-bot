package baraholkateam.command;

import baraholkateam.rest.service.AdvertisementService;
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
    private AdvertisementService advertisementService;

    public NewAdvertisementAddAdvertisementTypesCommand() {
        super(Command.NewAdvertisement_AddAdvertisementTypes.getName(),
                Command.NewAdvertisement_AddAdvertisementTypes.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareNextButton();
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.CHOSEN_CITY, advertisementService.getLastUserAdvertisement(chat.getId(), user.getId()).getTagsOfType(TagType.City)),
                true
        );

        prepareTags(TagType.AdvertisementType, true);
        sendAnswer(absSender, user, chat, Configuration.CommandMessage.ADD_TYPE_TEXT, true);
    }

}
