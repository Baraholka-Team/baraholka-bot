package baraholkateam.command;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class NewAdvertisementCommand extends BaraholkaBotCommand {

    @Autowired
    private final AdvertisementService advertisementService;

    public NewAdvertisementCommand(AdvertisementService advertisementService) {
        super(Command.NewAdvertisement.getName(), Command.NewAdvertisement.getDescription());
        this.advertisementService = advertisementService;
    }

    @Override
    public void executeCommand(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        prepareReplyKeyboard(List.of(Command.NewAdvertisement_AddPhotos.getDescription()), false);

        AdvertisementDTO lastUserAdvertisement = advertisementService.getLastUserAdvertisement(chat.getId(),  user.getId());
        if (lastUserAdvertisement == null) {
            AdvertisementDTO advertisementDTO = AdvertisementDTO.builder()
                    .chatId(chat.getId())
                    .userId(user.getId())
                    .messageId(messageId)
                    .build();
            advertisementService.saveNewAdvertisement(advertisementDTO);
        }

        sendAnswer(
                telegramClient,
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
