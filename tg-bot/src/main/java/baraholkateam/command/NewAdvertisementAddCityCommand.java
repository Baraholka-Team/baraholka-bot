package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.TagService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;

@Component
public class NewAdvertisementAddCityCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private TagService tagService;

    public NewAdvertisementAddCityCommand() {
        super(Command.NewAdvertisement_AddCity.getName(), Command.NewAdvertisement_AddCity.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chat.getId(), user.getId());
        advertisementDTO.addTag(tagService.getTagByName(Tag.Actual.getName()));
        advertisementService.saveNewAdvertisement(advertisementDTO);

        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_HASHTAGS_TEXT,
                true
        );

        prepareTags(TagType.City, false);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_CITY_TEXT,
                true
        );
    }

}
