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

import java.util.Objects;

@Component
public class NewAdvertisementAddCategoriesCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;

    public NewAdvertisementAddCategoriesCommand() {
        super(Command.NewAdvertisement_AddCategories.getIdentifier(),
                Command.NewAdvertisement_AddCategories.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String chosenAdvertisementTypeTags = currentAdvertisementService.get(chat.getId())
                .getTagsOfType(TagType.AdvertisementType);
        if (Objects.equals(chosenAdvertisementTypeTags, "")) {
            chosenAdvertisementTypeTags = Configuration.CommandMessage.NO_HASHTAGS;
        }

        prepareNextButton();
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(
                        Configuration.CommandMessage.CHOSEN_ADVERTISEMENT_TYPES,
                        chosenAdvertisementTypeTags
                ),
                true
        );

        prepareTags(TagType.ProductCategories, true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_CATEGORIES_TEXT,
                true
        );
    }

}
