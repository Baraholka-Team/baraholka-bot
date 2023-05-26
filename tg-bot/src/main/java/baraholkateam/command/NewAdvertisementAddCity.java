package baraholkateam.command;

import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.util.State;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewAdvertisementAddCity extends Command {
    private static final String ADD_HASHTAGS_TEXT = """
            Описание успешно добавлено.
            Теперь необходимо добавить хэштеги.""";
    private static final String ADD_CITY_TEXT = """
            Выберите город, который хотите добавить:""";

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;

    @Autowired
    private ChosenTagsService chosenTagsService;

    public NewAdvertisementAddCity() {
        super(State.NewAdvertisement_AddCity.getIdentifier(), State.NewAdvertisement_AddCity.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        currentAdvertisementService.setTags(chat.getId(), List.of(Tag.Actual.getName()));
        chosenTagsService.put(chat.getId(), new ArrayList<>());
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                ADD_HASHTAGS_TEXT, null);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                ADD_CITY_TEXT, getTags(TagType.City, false));
    }
}
