package baraholkateam.command;

import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class NewAdvertisementCommand extends BaraholkaBotCommand {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ChosenTagsService chosenTagsService;

    public NewAdvertisementCommand() {
        super(Command.NewAdvertisement.getName(), Command.NewAdvertisement.getDescription());
    }

    @Override
    public void executeCommand(AbsSender absSender, User user, Chat chat, String[] strings) {
        prepareReplyKeyboard(List.of(Command.NewAdvertisement_AddPhotos.getDescription()), false);
        sendAnswer(
                absSender,
                user,
                chat,
                String.format(
                        Configuration.CommandMessage.NEW_AD,
                        Command.NewAdvertisement.getName(),
                        Command.MainMenu.getName(),
                        Command.NewAdvertisement_AddPhotos.getDescription()
                ),
                true
        );
    }

}
