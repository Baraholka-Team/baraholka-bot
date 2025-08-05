package baraholkateam.command;

import baraholkateam.configuration.AdvertisementConfiguration;
import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ChosenTagsDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.StateService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

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
    @Autowired
    private AdvertisementConfiguration advertisementConfiguration;

    public SearchAdvertisementsShowFoundAdvertisementsCommand() {
        super(Command.SearchAdvertisements_ShowFoundAdvertisements.getName(),
                Command.SearchAdvertisements_ShowFoundAdvertisements.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) throws BaraholkaBotException {
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
                    telegramClient,
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
                        telegramClient,
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
                        telegramClient,
                        user,
                        chat,
                        String.format(
                                Configuration.CommandMessage.FOUND_ADVERTISEMENTS,
                                count,
                                advertisementConfiguration.getSearchAdvertisementLimit(),
                                Command.MainMenu.getName(),
                                Command.SearchAdvertisements.getName()
                        ),
                        true
                );
            }
        } else {
            sendAnswer(
                    telegramClient,
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
        List<AdvertisementDTO> sortedAdvertisementList = advertisementService.searchAdvertisementsWithTags(tags.getTags());
        int count = 0;
        for (AdvertisementDTO sortedAdvertisement : sortedAdvertisementList) {
            telegramAPIRequests.forwardMessage(String.valueOf(chatId), String.valueOf(chatId),
                    sortedAdvertisement.getMessageId());
            count++;
        }
        return count;
    }

    private void prepareCommandButtons(List<String> commands) {
        List<KeyboardRow> commandButtons = new ArrayList<>(commands.size());
        for (String command : commands) {
            KeyboardRow commandButton = new KeyboardRow();
            commandButton.add(new KeyboardButton(command));
            commandButtons.add(commandButton);
        }

        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup(commandButtons);
        rkm.setSelective(true);
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(true);

        replyKeyboard = rkm;
    }

}
