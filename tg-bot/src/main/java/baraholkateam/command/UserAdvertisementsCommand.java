package baraholkateam.command;

import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.repository.AdvertisementRepository;
import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class UserAdvertisementsCommand extends BaraholkaBotCommand {

    @Autowired
    private TelegramAPIRequests telegramAPIRequests;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private LastSentMessageService lastSentMessageService;
    @Value("${channel.chat_id}")
    private String channelChatId;

    public UserAdvertisementsCommand() {
        super(Command.UserAdvertisements.getIdentifier(), Command.UserAdvertisements.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        List<AdvertisementEntity> advertisementEntities = advertisementRepository
                .findAllByOwnerChatId(chat.getId());

        if (advertisementEntities != null && !advertisementEntities.isEmpty()) {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    Configuration.CommandMessage.USER_ADVERTISEMENTS
            );

            for (AdvertisementEntity advertisementEntity : advertisementEntities) {
                telegramAPIRequests.forwardMessage(
                        channelChatId,
                        String.valueOf(advertisementEntity.getOwnerChatId()),
                        advertisementEntity.getMessageId()
                );
            }
        } else {
            sendAnswer(
                    absSender,
                    user,
                    chat,
                    Configuration.CommandMessage.NO_ADVERTISEMENTS
            );
        }
    }

}
