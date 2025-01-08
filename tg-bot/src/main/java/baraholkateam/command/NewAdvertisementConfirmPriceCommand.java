package baraholkateam.command;

import baraholkateam.rest.service.AdvertisementService;
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
    private AdvertisementService advertisementService;

    public NewAdvertisementConfirmPriceCommand() {
        super(Command.NewAdvertisement_ConfirmPrice.getName(),
                Command.NewAdvertisement_ConfirmPrice.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(List.of(Command.NewAdvertisement_AddContacts.getDescription()), true);
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(Configuration.CommandMessage.CONFIRM_PRICE_TEXT, advertisementService.getLastUserAdvertisement(chat.getId(), user.getId()).getPrice()),
                true
        );
    }

}
