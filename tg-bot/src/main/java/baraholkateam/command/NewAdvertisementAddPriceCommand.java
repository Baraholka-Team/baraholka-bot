package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;
import java.util.Objects;

@Component
public class NewAdvertisementAddPriceCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;

    public NewAdvertisementAddPriceCommand() {
        super(Command.NewAdvertisement_AddPrice.getName(), Command.NewAdvertisement_AddPrice.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        String chosenCategoriesTags = advertisementService.getLastUserAdvertisement(chat.getId(), user.getId())
                .getTagsOfType(TagType.ProductCategories);
        if (Objects.equals(chosenCategoriesTags, "")) {
            chosenCategoriesTags = Configuration.CommandMessage.NO_HASHTAGS;
        }

        sendAnswer(
                telegramClient,
                user,
                chat,
                String.format(
                        Configuration.CommandMessage.CHOSEN_CATEGORIES,
                        chosenCategoriesTags
                )
        );

        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_PRICE_TEXT,
                true
        );
    }

    @Override
    public TextProcessResult processUserInput(TelegramClient telegramClient, Message message) {
        String text = message.getText();
        if (!text.matches("\\d{1,18}")) {
            return new TextProcessResult(true, Configuration.CommandMessage.PRICE_NOT_VALID);
        }
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
        advertisementDTO.setPrice(Long.parseLong(text));
        advertisementService.saveNewAdvertisement(advertisementDTO);
        return new TextProcessResult(false, Configuration.CommandMessage.PRICE_ADDED);
    }

}
