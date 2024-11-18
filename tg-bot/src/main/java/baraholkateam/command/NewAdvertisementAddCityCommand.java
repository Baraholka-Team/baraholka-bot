package baraholkateam.command;

import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NewAdvertisementAddCityCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;
    @Autowired
    private ChosenTagsService chosenTagsService;

    public NewAdvertisementAddCityCommand() {
        super(Command.NewAdvertisement_AddCity.getIdentifier(), Command.NewAdvertisement_AddCity.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        currentAdvertisementService.setTags(chat.getId(), List.of(Tag.Actual.getName()));
        chosenTagsService.put(chat.getId(), new ArrayList<>());

        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_HASHTAGS_TEXT,
                true
        );

        prepareTags(TagType.City, false);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_CITY_TEXT,
                true
        );
    }

}
