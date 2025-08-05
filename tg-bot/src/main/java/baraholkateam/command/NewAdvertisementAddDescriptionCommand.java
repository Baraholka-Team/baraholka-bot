package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NewAdvertisementAddDescriptionCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Value("${advertisement.description_length}")
    private int descriptionLength;

    public NewAdvertisementAddDescriptionCommand() {
        super(Command.NewAdvertisement_AddDescription.getName(),
                Command.NewAdvertisement_AddDescription.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_DESCRIPTION_TEXT,
                true
        );
    }

    @Override
    public TextProcessResult processUserInput(TelegramClient telegramClient, Message message) {
        String text = message.getText();
        if (text == null || text.trim().isEmpty() || text.length() > descriptionLength) {
            return new TextProcessResult(
                    true,
                    Configuration.CommandMessage.DESCRIPTION_TOO_LONG.formatted(descriptionLength)
            );
        }
        Pattern filter = Pattern.compile(Configuration.CommandMessage.SWEAR_WORD_DETECTOR, Pattern.CASE_INSENSITIVE);
        Matcher matcher = filter.matcher(text);
        if (matcher.find()) {
            String firstSwearWord = matcher.results()
                    .findFirst()
                    .map(MatchResult::group)
                    .orElse(Configuration.CommandMessage.EMPTY_STRING);
            return new TextProcessResult(
                   true,
                   Configuration.CommandMessage.ADVERTISEMENT_SWEAR_WORD_DETECTED.formatted(firstSwearWord)
            );
        }
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
        advertisementDTO.setDescription(message.getText());
        advertisementService.saveNewAdvertisement(advertisementDTO);
        return new TextProcessResult(false, Configuration.CommandMessage.DESCRIPTION_ADDED);
    }

}
