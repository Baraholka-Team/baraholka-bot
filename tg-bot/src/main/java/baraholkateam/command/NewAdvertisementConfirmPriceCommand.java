package baraholkateam.command;

import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class NewAdvertisementConfirmPriceCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;

    public NewAdvertisementConfirmPriceCommand() {
        super(Command.NewAdvertisement_ConfirmPrice.getIdentifier(),
                Command.NewAdvertisement_ConfirmPrice.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(List.of(Command.NewAdvertisement_AddContacts.getDescription()), true);
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.CONFIRM_PRICE_TEXT, currentAdvertisementService.get(chat.getId()).getPrice()),
                true
        );
    }

}
