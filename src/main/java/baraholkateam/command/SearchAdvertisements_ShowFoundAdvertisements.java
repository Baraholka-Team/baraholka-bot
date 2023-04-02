package baraholkateam.command;

import baraholkateam.bot.BaraholkaBotProperties;
import baraholkateam.database.SQLExecutor;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.State;
import baraholkateam.util.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static baraholkateam.bot.BaraholkaBot.SEARCH_ADVERTISEMENTS_LIMIT;

public class SearchAdvertisements_ShowFoundAdvertisements extends Command {
    private static final String CANNOT_FIND_ADVERTISEMENTS = """
            По Вашему запросу ничего не нашлось.
            Вы можете вернуться в главное меню /%s или найти объявления по другим хэштегам /%s.""";
    private static final String FOUND_ADVERTISEMENTS = """
            По Вашему запросу нашлось объявлений: %d.
            Показывается не более %s самых актуальных объявлений.
            Вы можете вернуться в главное меню /%s или найти объявления по другим хэштегам /%s.""";
    private final Map<Long, String> chosenTags;
    private final SQLExecutor sqlExecutor;
    private final TelegramAPIRequests telegramAPIRequests;
    private final Logger logger = LoggerFactory.getLogger(SearchAdvertisements_ShowFoundAdvertisements.class);

    public SearchAdvertisements_ShowFoundAdvertisements(String commandIdentifier, String description,
                                                        Map<Long, Message> lastSentMessage,
                                                        Map<Long, String> chosenTags,
                                                        SQLExecutor sqlExecutor) {
        super(commandIdentifier, description, lastSentMessage);
        this.chosenTags = chosenTags;
        this.sqlExecutor = sqlExecutor;
        telegramAPIRequests = new TelegramAPIRequests();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        int count = forwardMessages(chat.getId());

        String chosenTagsString = chosenTags.get(chat.getId());

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                String.format(CHOSEN_HASHTAGS, chosenTagsString == null ? NO_HASHTAGS : chosenTagsString), null);
        if (count == 0) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                    String.format(CANNOT_FIND_ADVERTISEMENTS,
                            State.MainMenu.getIdentifier(),
                            State.SearchAdvertisements.getIdentifier()),
                    showCommandButtons(List.of(State.MainMenu.getDescription(),
                            State.SearchAdvertisements.getDescription())));
        } else {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                    String.format(FOUND_ADVERTISEMENTS,
                            count,
                            SEARCH_ADVERTISEMENTS_LIMIT,
                            State.MainMenu.getIdentifier(),
                            State.SearchAdvertisements.getIdentifier()),
                    showCommandButtons(List.of(State.MainMenu.getDescription(),
                            State.SearchAdvertisements.getDescription())));
        }
    }

    private int forwardMessages(Long chatId) {
        if (chosenTags.get(chatId) == null) {
            return 0;
        }
        ResultSet messageIds = sqlExecutor.tagsSearch(Arrays.stream(chosenTags.get(chatId).split(" "))
                .map(Tag::getTagByName)
                .toList());
        int count = 0;
        while (true) {
            try {
                if (!messageIds.next()) break;
                telegramAPIRequests.forwardMessage(BaraholkaBotProperties.CHANNEL_USERNAME, String.valueOf(chatId),
                        messageIds.getLong("message_id"));
                count++;
            } catch (SQLException e) {
                logger.error(String.format("Cannot handle sql: %s", e.getMessage()));
                throw new RuntimeException("Failed to handle sql", e);
            }
        }
        return count;
    }

    private ReplyKeyboardMarkup showCommandButtons(List<String> commands) {
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
        return rkm;
    }
}
