package baraholkateam.command;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.StateService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.ContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class NewAdvertisementConfirmPhoneCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private StateService stateService;

    public NewAdvertisementConfirmPhoneCommand() {
        super(Command.NewAdvertisement_ConfirmPhone.getName(), Command.NewAdvertisement_ConfirmPhone.getDescription());
    }

    @Override
    public void processReplyKeyboardCommandText(TelegramClient telegramClient, Message message) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        if (message.hasText() && Objects.equals(message.getText(), Configuration.CommandMessage.DELETE_ALL_SOCIALS)) {
            AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
            advertisementDTO.setContacts(new ArrayList<>());
            advertisementService.saveNewAdvertisement(advertisementDTO);
            sendAnswer(telegramClient, message.getFrom(), message.getChat(), Configuration.CommandMessage.SOCIALS_DELETE);
        }
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) throws BaraholkaBotException {
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chat.getId(), user.getId());
        ContactDTO phone = advertisementDTO.getContacts().stream()
                .filter(contactDTO -> contactDTO.getContactType().getContactTypeName().equals(ContactType.Phone))
                .findFirst()
                .orElse(null);
        List<ContactDTO> socials = advertisementDTO.getContacts();
        Command previousCommand = stateService.getState(chat.getId(), user.getId()).getPreviousCommand().getCommand();
        if (phone != null && previousCommand != null && !previousCommand.equals(Command.NewAdvertisement_AddSocial)) {
            prepareReplyKeyboard(Collections.emptyList(), true);
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.PHONE_TEXT, phone),
                    true
            );
        } else if (!socials.isEmpty()) {
            prepareReplyKeyboard(List.of(Configuration.CommandMessage.DELETE_ALL_SOCIALS), true);
            sendAnswer(
                    telegramClient,
                    user,
                    chat,
                    String.format(Configuration.CommandMessage.SOCIAL_TEXT, socials.get(socials.size() - 1)),
                    true
            );
        }

        prepareAddSocial();
        sendAnswer(
                telegramClient,
                user,
                chat,
                Configuration.CommandMessage.CONFIRM_PHONE_TEXT,
                true
        );
    }

    private void prepareAddSocial() {
        InlineKeyboardButton yesButton = new InlineKeyboardButton("Да");
        String yesCallbackData = String.format("%s %s", Configuration.CommandMessage.SOCIAL_CALLBACK_DATA, "yes");
        yesButton.setCallbackData(yesCallbackData);

        InlineKeyboardButton noButton = new InlineKeyboardButton("Нет");
        String noCallbackData = String.format("%s %s", Configuration.CommandMessage.SOCIAL_CALLBACK_DATA, "no");
        noButton.setCallbackData(noCallbackData);

        InlineKeyboardRow keyboardFirstRow = new InlineKeyboardRow();
        keyboardFirstRow.add(yesButton);
        keyboardFirstRow.add(noButton);

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardFirstRow);

        replyKeyboard = new InlineKeyboardMarkup(keyboardRows);
    }

}
