package baraholkateam.command;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.ChosenTagsDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.StateService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchAdvertisementsShowFoundAdvertisementsCommand extends BaraholkaBotCommand {

    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private TelegramAPIRequests telegramAPIRequests;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private StateService stateService;
    @Value("${channel.username}")
    private String channelUsername;
    @Value("${search_advertisement_limit}")
    private Integer searchAdvertisementsLimit;

    public SearchAdvertisementsShowFoundAdvertisementsCommand() {
        super(Command.SearchAdvertisements_ShowFoundAdvertisements.getName(),
                Command.SearchAdvertisements_ShowFoundAdvertisements.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] arguments) throws BaraholkaBotException {
        ChosenTagsDTO tags = chosenTagsService.getChosenTags(chat.getId(), user.getId());
        Command previousCommand = stateService.getState(chat.getId(), user.getId()).getPreviousCommand().getCommand();
        if (previousCommand != null && previousCommand.equals(Command.SearchAdvertisements_AddProductCategories)) {
            String hashtags = Configuration.CommandMessage.NO_HASHTAGS;
            if (tags != null && !tags.getTags().isEmpty()) {
                hashtags = tags.getTags().stream()
                        .map(TagDTO::getTag)
                        .map(Tag::getName)
                        .collect(Collectors.joining(" "));
            }

            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CHOSEN_HASHTAGS, hashtags)
            );

            int count = forwardMessages(chat.getId(), user.getId());

            if (count == 0) {
                prepareCommandButtons(
                        List.of(
                                Command.MainMenu.getDescription(),
                                Command.SearchAdvertisements.getDescription()
                        )
                );
                sendAnswer(
                        absSender,
                        user,
                        chat,
                        String.format(
                                Configuration.CommandMessage.CANNOT_FIND_ADVERTISEMENTS,
                                Command.MainMenu.getName(),
                                Command.SearchAdvertisements.getName()
                        ),
                        true);
            } else {
                prepareCommandButtons(
                        List.of(
                                Command.MainMenu.getDescription(),
                                Command.SearchAdvertisements.getDescription()
                        )
                );
                sendAnswer(
                        absSender,
                        user,
                        chat,
                        String.format(
                                Configuration.CommandMessage.FOUND_ADVERTISEMENTS,
                                count,
                                searchAdvertisementsLimit,
                                Command.MainMenu.getName(),
                                Command.SearchAdvertisements.getName()
                        ),
                        true
                );
            }
        } else {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.INCORRECT_PREVIOUS_STATE, Command.MainMenu.getName())
            );
        }
    }

    private int forwardMessages(Long chatId, Long userId) {
        ChosenTagsDTO tags = chosenTagsService.getChosenTags(chatId, userId);
        if (tags == null || tags.getTags().isEmpty()) {
            return 0;
        }
        List<AdvertisementEntity> sortedAds = advertisementService.searchAdvertisementsWithTags(tags.getTags());
        int count = 0;
        for (AdvertisementEntity sortedAd : sortedAds) {
            telegramAPIRequests.forwardMessage(channelUsername, String.valueOf(chatId),
                    sortedAd.getMessageId());
            count++;
        }
        return count;
    }

    private void prepareCommandButtons(List<String> commands) {
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setSelective(true);
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(true);
        List<KeyboardRow> commandButtons = new ArrayList<>(commands.size());
        for (String command : commands) {
            KeyboardRow commandButton = new KeyboardRow();
            commandButton.add(new KeyboardButton(command));
            commandButtons.add(commandButton);
        }
        rkm.setKeyboard(commandButtons);

        replyKeyboard = rkm;
    }

}
