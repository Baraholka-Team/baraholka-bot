package baraholkateam.command;

import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.PreviousStateService;
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

import static baraholkateam.bot.BaraholkaBot.SEARCH_ADVERTISEMENTS_LIMIT;

@Component
public class SearchAdvertisementsShowFoundAdvertisementsCommand extends BaraholkaBotCommand {

    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private TelegramAPIRequests telegramAPIRequests;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private PreviousStateService previousStateService;
    @Value("${channel.username}")
    private String channelUsername;

    public SearchAdvertisementsShowFoundAdvertisementsCommand() {
        super(Command.SearchAdvertisements_ShowFoundAdvertisements.getIdentifier(),
                Command.SearchAdvertisements_ShowFoundAdvertisements.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        List<Tag> tags = chosenTagsService.get(chat.getId());

        if (previousStateService.get(chat.getId()) == Command.SearchAdvertisements_AddProductCategories) {
            String hashtags = Configuration.CommandMessage.NO_HASHTAGS;
            if (tags != null && !tags.isEmpty()) {
                hashtags = tags.stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(" "));
            }

            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.CHOSEN_HASHTAGS, hashtags)
            );

            int count = forwardMessages(chat.getId());

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
                                Command.MainMenu.getIdentifier(),
                                Command.SearchAdvertisements.getIdentifier()
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
                                SEARCH_ADVERTISEMENTS_LIMIT,
                                Command.MainMenu.getIdentifier(),
                                Command.SearchAdvertisements.getIdentifier()
                        ),
                        true
                );
            }
        } else {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.INCORRECT_PREVIOUS_STATE, Command.MainMenu.getIdentifier())
            );
        }
    }

    private int forwardMessages(Long chatId) {
        List<Tag> tags = chosenTagsService.get(chatId);
        if (tags == null || tags.isEmpty()) {
            return 0;
        }
        List<AdvertisementEntity> sortedAds = advertisementService.tagsSearch(tags.stream()
                .map(Tag::getName)
                .toArray(String[]::new));
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
