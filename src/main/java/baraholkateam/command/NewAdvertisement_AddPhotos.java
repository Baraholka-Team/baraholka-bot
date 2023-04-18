package baraholkateam.command;

import baraholkateam.util.State;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

public class NewAdvertisement_AddPhotos extends Command {
    private static final String ADD_PHOTOS_TEXT = """
            Добавьте от 1 до 10 фотографий к вашему объявлению. Рекомендуемое число - 5.""";

    public NewAdvertisement_AddPhotos(Map<Long, Message> lastSentMessage) {
        super(State.NewAdvertisement_AddPhotos.getIdentifier(), State.NewAdvertisement_AddPhotos.getDescription(),
                lastSentMessage);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), user.getUserName(),
                ADD_PHOTOS_TEXT, null);
    }
}