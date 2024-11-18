package baraholkateam.command;

import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.PreviousStateService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchAdvertisementsAddAdvertisementTypes extends BaraholkaBotCommand {

    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private PreviousStateService previousStateService;

    public SearchAdvertisementsAddAdvertisementTypes() {
        super(Command.SearchAdvertisements_AddAdvertisementTypes.getIdentifier(),
                Command.SearchAdvertisements_AddAdvertisementTypes.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        List<Tag> tags = chosenTagsService.get(chat.getId());

        if (previousStateService.get(chat.getId()) == Command.SearchAdvertisements) {
            String hashtags = Configuration.CommandMessage.NO_HASHTAGS;
            if (tags != null && !tags.isEmpty()) {
                hashtags = tags.stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(" "));
            }

            prepareNextButton();
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CHOSEN_HASHTAGS, hashtags),
                    true
            );

            prepareTags(TagType.AdvertisementType, true);
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CHOOSE_ADVERTISEMENT_TYPE, Configuration.CommandMessage.NEXT_BUTTON_TEXT),
                    true
            );
        } else {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.INCORRECT_PREVIOUS_STATE, Command.MainMenu.getIdentifier())
            );
        }
    }

}
