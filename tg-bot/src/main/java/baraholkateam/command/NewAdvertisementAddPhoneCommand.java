package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.ContactTypeDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ContactService;
import baraholkateam.rest.service.ContactTypeService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.ContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;

@Component
public class NewAdvertisementAddPhoneCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactTypeService contactTypeService;
    @Value("${advertisement.phone.regexp}")
    private String phoneRegularExpression;

    public NewAdvertisementAddPhoneCommand() {
        super(Command.NewAdvertisement_AddPhone.getName(), Command.NewAdvertisement_AddPhone.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(Collections.emptyList(), true);
        sendAnswer(
                absSender,
                user,
                chat,
                Configuration.CommandMessage.ADD_PHONE_TEXT,
                true
        );
    }

    @Override
    public TextProcessResult processUserInput(AbsSender absSender, Message message) {
        String text = message.getText();
        if (!text.matches(phoneRegularExpression)) {
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
