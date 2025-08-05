package baraholkateam.command;

import baraholkateam.configuration.AdvertisementConfiguration;
import baraholkateam.configuration.BaraholkaBotConfiguration;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.ContactTypeDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ContactTypeService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.ContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;

@Component
public class NewAdvertisementAddPhoneCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ContactTypeService contactTypeService;
    @Autowired
    private AdvertisementConfiguration advertisementConfiguration;

    public NewAdvertisementAddPhoneCommand() {
        super(Command.NewAdvertisement_AddPhone.getName(), Command.NewAdvertisement_AddPhone.getDescription());
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.ADD_PHONE_TEXT,
                true
        );
    }

    @Override
    public TextProcessResult processUserInput(TelegramClient telegramClient, Message message) {
        String text = message.getText();
        if (!text.matches(advertisementConfiguration.getPhone())) {
            return new TextProcessResult(true, Configuration.CommandMessage.PHONE_NOT_VALID);
        }
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
        ContactTypeDTO contactTypeDTO = contactTypeService.getContactTypeByName(ContactType.Phone.getName());
        ContactDTO contactDTO = ContactDTO.builder()
                .contactType(contactTypeDTO)
                .contactName(text)
                .build();
        advertisementDTO.addContact(contactDTO);
        advertisementService.saveNewAdvertisement(advertisementDTO);
        return new TextProcessResult(false, Configuration.CommandMessage.PHONE_ADDED);
    }

}
