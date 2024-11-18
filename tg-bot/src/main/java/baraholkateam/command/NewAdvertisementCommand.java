package baraholkateam.command;

import baraholkateam.rest.model.CurrentAdvertisement;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class NewAdvertisementCommand extends BaraholkaBotCommand {

    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;
    @Autowired
    private ChosenTagsService chosenTagsService;

    public NewAdvertisementCommand() {
        super(Command.NewAdvertisement.getIdentifier(), Command.NewAdvertisement.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        currentAdvertisementService.put(new CurrentAdvertisement(chat.getId()));
        chosenTagsService.delete(chat.getId());

        prepareReplyKeyboard(List.of(Command.NewAdvertisement_AddPhotos.getDescription()), false);
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(
                        Configuration.CommandMessage.NEW_AD,
                        Command.NewAdvertisement.getIdentifier(),
                        Command.MainMenu.getIdentifier(),
                        Command.NewAdvertisement_AddPhotos.getDescription()
                ),
                true
        );
    }

}
