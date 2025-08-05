package baraholkateam.command;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.ChosenTagsDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.StateService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Component
public class SearchAdvertisementsAddAdvertisementTypesCommand extends BaraholkaBotCommand {

    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private StateService stateService;

    public SearchAdvertisementsAddAdvertisementTypesCommand() {
        super(Command.SearchAdvertisements_AddAdvertisementTypes.getName(),
                Command.SearchAdvertisements_AddAdvertisementTypes.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) throws BaraholkaBotException {
        ChosenTagsDTO tags = chosenTagsService.getChosenTags(chat.getId(), user.getId());
        Command previousCommand = stateService.getState(chat.getId(), user.getId()).getPreviousCommand().getCommand();
        if (previousCommand != null && previousCommand.equals(Command.SearchAdvertisements)) {
            String hashtags = Configuration.CommandMessage.NO_HASHTAGS;
            if (tags != null && !tags.getTags().isEmpty()) {
                hashtags = tags.getTags().stream()
                        .map(TagDTO::getTag)
                        .map(Tag::getName)
                        .collect(Collectors.joining(" "));
            }

            prepareNextButton();
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CHOSEN_HASHTAGS, hashtags),
                    true
            );

            prepareTags(TagType.AdvertisementType, true);
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CHOOSE_ADVERTISEMENT_TYPE, Configuration.Buttons.NEXT_BUTTON),
                    true
            );
        } else {
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.INCORRECT_PREVIOUS_STATE, Command.MainMenu.getName())
            );
        }
    }

}
