package baraholkateam.notification;

import baraholkateam.bot.BaraholkaBot;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.NotificationMessagesService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NotificationExecutor {

    /**
     * Период времени до первого уведомления пользователя о подтверждении актуальности объявления.
     */
    public static final Long FIRST_REPEAT_NOTIFICATION_PERIOD = 14L;
    /**
     * Период времени до первого уведомления пользователя о подтверждении актуальности объявления.
     */
    public static final TimeUnit FIRST_REPEAT_NOTIFICATION_TIME_UNIT = TimeUnit.DAYS;
    /**
     * Период времени до повторных уведомлений пользователя о подтверждении актуальности объявления.
     */
    private static final Long REPEAT_NOTIFICATION_PERIOD = 24L;
    /**
     * Период времени до повторных уведомлений пользователя о подтверждении актуальности объявления.
     */
    private static final TimeUnit REPEAT_NOTIFICATION_TIME_UNIT = TimeUnit.HOURS;

    @Autowired
    private TelegramAPIRequests telegramAPIRequests;

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private NotificationMessagesService notificationMessagesService;

    @Value("${channel.username}")
    private String channelUsername;

    @Value("${channel.chat_id}")
    private String channelChatId;

    private final BaraholkaBot sender;

    @Lazy
    @Autowired
    public NotificationExecutor(BaraholkaBot sender) {
        this.sender = sender;
    }

    @Scheduled(initialDelayString = "${notificator.initial-delay-in-milliseconds}",
            fixedRateString = "${notificator.fixed-rate-in-milliseconds}")
    private void startNotificationExecutor() {
        List<AdvertisementEntity> advertisementEntities = advertisementService.askActualAdvertisements(System.currentTimeMillis());
        for (AdvertisementEntity advertisementEntity : advertisementEntities) {
            int attemptNum = advertisementEntity.getUpdateAttempt();
            long chatId = advertisementEntity.getOwnerChatId();
            long messageId = advertisementEntity.getMessageId();
            if (attemptNum == 3) {
                editAdText(sender, String.valueOf(messageId));
                advertisementService.removeAdvertisement(messageId);
                deleteMessages(sender, chatId, messageId);
                sendMessageWithoutDelete(sender, chatId, Configuration.CommandMessage.ADVERTISEMENT_DELETE, null);
            } else if (attemptNum <= 2) {
                advertisementService.setUpdateAttempt(messageId, attemptNum + 1);
                Long forwardedMessageId =
                        telegramAPIRequests.forwardMessage(channelUsername, String.valueOf(chatId), messageId);

                if (forwardedMessageId == null) {
                    log.error(Configuration.NotificationMessage.CANNOT_FORWARD_MESSAGE);
                    break;
                }

                if (attemptNum == 2) {
                    deleteMessages(sender, chatId, messageId);
                    sendMessage(sender, chatId, messageId,
                            String.format("%s\n%s", String.format(Configuration.NotificationMessage.DELETE_IF_NOT_UPDATE,
                                    REPEAT_NOTIFICATION_PERIOD), Configuration.NotificationMessage.ASK_NEXT_UPDATE), ifNextUpdate(chatId,
                                    messageId,
                                    REPEAT_NOTIFICATION_TIME_UNIT.toMillis(REPEAT_NOTIFICATION_PERIOD)));
                } else if (attemptNum == 1) {
                    deleteMessages(sender, chatId, messageId);
                    sendMessage(sender, chatId, messageId, Configuration.NotificationMessage.ASK_NEXT_UPDATE, ifNextUpdate(chatId, messageId,
                                    REPEAT_NOTIFICATION_TIME_UNIT.toMillis(REPEAT_NOTIFICATION_PERIOD)));
                } else {
                    sendMessage(sender, chatId, messageId, Configuration.NotificationMessage.ASK_NEXT_UPDATE, ifNextUpdate(chatId, messageId,
                                    FIRST_REPEAT_NOTIFICATION_TIME_UNIT
                                            .toMillis(FIRST_REPEAT_NOTIFICATION_PERIOD)));
                }

                Message forwardedMessage = new Message();
                forwardedMessage.setMessageId(Math.toIntExact(forwardedMessageId));
                Chat chat = new Chat();
                chat.setId(chatId);
                forwardedMessage.setChat(chat);

                addNotificationMessage(forwardedMessage, chatId, messageId);
            } else {
                log.error("Incorrect number of update attempt: {}. Chat id: {}, message id: {}", attemptNum, chatId, messageId);
            }
        }
    }

    public void deleteMessages(AbsSender absSender, Long chatId, Long messageId) {
        DeleteMessage deleteLastMessage = new DeleteMessage();
        if (notificationMessagesService.get(chatId) != null) {
            for (Message message : notificationMessagesService.get(chatId).get(messageId)) {
                deleteLastMessage.setMessageId(message.getMessageId());
                deleteLastMessage.setChatId(message.getChatId());
                try {
                    absSender.execute(deleteLastMessage);
                } catch (TelegramApiException e) {
                    log.error("Cannot delete message due to: {}", e.getMessage());
                }
            }
            notificationMessagesService.removeMessage(chatId, messageId);
        }
    }

    private void sendMessage(AbsSender sender, long chatId, long messageId, String text,
                                    InlineKeyboardMarkup buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (buttons != null) {
            message.setReplyMarkup(buttons);
        }
        try {
            Message sendedMessage = sender.execute(message);
            addNotificationMessage(sendedMessage, chatId, messageId);
        } catch (TelegramApiException e) {
            log.error("Cannot send message: {}", e.getMessage());
        }
    }

    private void sendMessageWithoutDelete(AbsSender sender, long chatId, String text,
                                                 InlineKeyboardMarkup buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (buttons != null) {
            message.setReplyMarkup(buttons);
        }
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Cannot send message without delete: {}", e.getMessage());
        }
    }

    private void addNotificationMessage(Message message, Long chatId, Long messageId) {
        Map<Long, List<Message>> currentMessagesMap = notificationMessagesService.get(chatId);
        if (currentMessagesMap == null) {
            Map<Long, List<Message>> newMessagesMap = new ConcurrentHashMap<>();
            List<Message> newMessage = new CopyOnWriteArrayList<>();
            newMessage.add(message);
            newMessagesMap.put(messageId, newMessage);
            notificationMessagesService.put(chatId, newMessagesMap);
        } else {
            List<Message> findMessages = currentMessagesMap.get(messageId);
            List<Message> currentMessage;
            if (findMessages == null) {
                currentMessage = new CopyOnWriteArrayList<>();
            } else {
                currentMessage = new CopyOnWriteArrayList<>(currentMessagesMap.get(messageId));
            }
            currentMessage.add(message);
            currentMessagesMap.put(messageId, currentMessage);
            notificationMessagesService.put(chatId, currentMessagesMap);
        }
    }

    private InlineKeyboardMarkup ifNextUpdate(long chatId, long messageId, long addNextUpdateTime) {
        List<List<InlineKeyboardButton>> answers = new ArrayList<>(1);
        List<InlineKeyboardButton> yesNoAnswer = new ArrayList<>(1);
        yesNoAnswer.add(InlineKeyboardButton.builder()
                .text("Да")
                .callbackData(String.format("%s %d %d %d 1", Configuration.CommandMessage.NOTIFICATION_CALLBACK_DATA, chatId, messageId,
                        addNextUpdateTime))
                .build());
        yesNoAnswer.add(InlineKeyboardButton.builder()
                .text("Нет")
                .callbackData(String.format("%s %d %d 0 0", Configuration.CommandMessage.NOTIFICATION_CALLBACK_DATA, chatId, messageId))
                .build());
        answers.add(yesNoAnswer);
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        ikm.setKeyboard(answers);
        return ikm;
    }

    private void editAdText(AbsSender absSender, String messageId) {
        EditMessageCaption editMessage = new EditMessageCaption();
        String editedText = String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT,
                advertisementService.adText(Long.parseLong(messageId))
                .substring(Tag.Actual.getName().length() + 1));
        editMessage.setChatId(channelChatId);
        editMessage.setMessageId(Integer.parseInt(messageId));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setCaption(editedText);

        try {
            absSender.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot edit deleted message: {}", e.getMessage());
        }
    }

}
