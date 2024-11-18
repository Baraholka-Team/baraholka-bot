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

import java.util.Collections;
import java.util.Objects;

@Component
public class NewAdvertisementAddPriceCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;

    public NewAdvertisementAddPriceCommand() {
        super(Command.NewAdvertisement_AddPrice.getIdentifier(), Command.NewAdvertisement_AddPrice.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String chosenCategoriesTags = currentAdvertisementService.get(chat.getId())
                .getTagsOfType(TagType.ProductCategories);
        if (Objects.equals(chosenCategoriesTags, "")) {
            chosenCategoriesTags = Configuration.CommandMessage.NO_HASHTAGS;
        }

        sendAnswer(
                absSender,
                user,
                chat,
                String.format(
                        Configuration.CommandMessage.CHOSEN_CATEGORIES,
                        chosenCategoriesTags
                )
        );

        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_PRICE_TEXT,
                true
        );
    }

}
