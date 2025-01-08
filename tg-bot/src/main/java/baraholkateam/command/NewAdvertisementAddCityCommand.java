package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.TagService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;

@Component
public class NewAdvertisementAddCityCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private TagService tagService;

    public NewAdvertisementAddCityCommand() {
        super(Command.NewAdvertisement_AddCity.getName(), Command.NewAdvertisement_AddCity.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chat.getId(), user.getId());
        advertisementDTO.addTag(tagService.getTagByName(Tag.Actual.getName()));
        advertisementService.saveNewAdvertisement(advertisementDTO);

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
