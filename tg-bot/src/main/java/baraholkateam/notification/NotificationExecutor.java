package baraholkateam.notification;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
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
    private final TelegramClient telegramClient;

    public NotificationExecutor(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Scheduled(initialDelayString = "${notificator.initial-delay-in-milliseconds}",
            fixedRateString = "${notificator.fixed-rate-in-milliseconds}")
    private void startNotificationExecutor() {
        List<AdvertisementDTO> advertisementDTOList = advertisementService.getMarkedForDeleteAdvertisements(System.currentTimeMillis());
        for (AdvertisementDTO advertisementDTO : advertisementDTOList) {
            int attemptNum = advertisementDTO.getUpdateAttempt();
            long chatId = advertisementDTO.getChatId();
            long userId = advertisementDTO.getUserId();
            int messageId = advertisementDTO.getMessageId();
            if (attemptNum == 3) {
                editAdText(chatId, userId, String.valueOf(messageId));
                advertisementService.removeAdvertisement(chatId, messageId);
                deleteMessages(chatId, messageId);
                sendMessageWithoutDelete(telegramClient, chatId, Configuration.CommandMessage.ADVERTISEMENT_DELETE, null);
            } else if (attemptNum <= 2) {
//                advertisementService.setUpdateAttempt(messageId, attemptNum + 1);
                Long forwardedMessageId =
                        telegramAPIRequests.forwardMessage(String.valueOf(chatId), String.valueOf(chatId), messageId);

                if (forwardedMessageId == null) {
                    log.error(Configuration.NotificationMessage.CANNOT_FORWARD_MESSAGE);
                    break;
                }

                if (attemptNum == 2) {
                    deleteMessages(chatId, messageId);
                    sendMessage(chatId, messageId,
                            String.format("%s\n%s", String.format(Configuration.NotificationMessage.DELETE_IF_NOT_UPDATE,
                                    REPEAT_NOTIFICATION_PERIOD), Configuration.NotificationMessage.ASK_NEXT_UPDATE), ifNextUpdate(chatId,
                                    messageId,
                                    REPEAT_NOTIFICATION_TIME_UNIT.toMillis(REPEAT_NOTIFICATION_PERIOD)));
                } else if (attemptNum == 1) {
                    deleteMessages(chatId, messageId);
                    sendMessage(chatId, messageId, Configuration.NotificationMessage.ASK_NEXT_UPDATE, ifNextUpdate(chatId, messageId,
                                    REPEAT_NOTIFICATION_TIME_UNIT.toMillis(REPEAT_NOTIFICATION_PERIOD)));
                } else {
                    sendMessage(chatId, messageId, Configuration.NotificationMessage.ASK_NEXT_UPDATE, ifNextUpdate(chatId, messageId,
                                    FIRST_REPEAT_NOTIFICATION_TIME_UNIT
                                            .toMillis(FIRST_REPEAT_NOTIFICATION_PERIOD)));
                }

                Message forwardedMessage = new Message();
                forwardedMessage.setMessageId(Math.toIntExact(forwardedMessageId));
                Chat chat = new Chat(chatId, "group"); // TODO сделать выбор типа чата
                forwardedMessage.setChat(chat);

                addNotificationMessage(forwardedMessage, chatId, messageId);
            } else {
                log.error("Incorrect number of update attempt: {}. Chat id: {}, message id: {}", attemptNum, chatId, messageId);
            }
        }
    }

    public void deleteMessages(Long chatId, Integer messageId) {
        DeleteMessage deleteLastMessage = new DeleteMessage(String.valueOf(chatId), messageId);
//        if (notificationMessagesService.get(chatId) != null) {
//            for (Message message : notificationMessagesService.get(chatId).get(messageId)) {
//                deleteLastMessage.setMessageId(message.getMessageId());
//                deleteLastMessage.setChatId(message.getChatId());
//                try {
//                    telegramClient.execute(deleteLastMessage);
//                } catch (TelegramApiException e) {
//                    log.error("Cannot delete message due to: {}", e.getMessage());
//                }
//            }
//            notificationMessagesService.removeMessage(chatId, messageId);
//        }
    }

    private void sendMessage(long chatId, int messageId, String text,
                                    InlineKeyboardMarkup buttons) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        if (buttons != null) {
            message.setReplyMarkup(buttons);
        }
        try {
            Message sendedMessage = telegramClient.execute(message);
            addNotificationMessage(sendedMessage, chatId, messageId);
        } catch (TelegramApiException e) {
            log.error("Cannot send message: {}", e.getMessage());
        }
    }

    private void sendMessageWithoutDelete(TelegramClient telegramClient, long chatId, String text, InlineKeyboardMarkup buttons) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        if (buttons != null) {
            message.setReplyMarkup(buttons);
        }
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Cannot send message without delete: {}", e.getMessage());
        }
    }

    private void addNotificationMessage(Message message, Long chatId, Integer messageId) {
//        Map<Integer, List<Message>> currentMessagesMap = notificationMessagesService.get(chatId);
//        if (currentMessagesMap == null) {
//            Map<Integer, List<Message>> newMessagesMap = new ConcurrentHashMap<>();
//            List<Message> newMessage = new CopyOnWriteArrayList<>();
//            newMessage.add(message);
//            newMessagesMap.put(messageId, newMessage);
//            notificationMessagesService.put(chatId, newMessagesMap);
//        } else {
//            List<Message> findMessages = currentMessagesMap.get(messageId);
//            List<Message> currentMessage;
//            if (findMessages == null) {
//                currentMessage = new CopyOnWriteArrayList<>();
//            } else {
//                currentMessage = new CopyOnWriteArrayList<>(currentMessagesMap.get(messageId));
//            }
//            currentMessage.add(message);
//            currentMessagesMap.put(messageId, currentMessage);
//            notificationMessagesService.put(chatId, currentMessagesMap);
//        }
    }

    private InlineKeyboardMarkup ifNextUpdate(long chatId, long messageId, long addNextUpdateTime) {
        List<InlineKeyboardRow> answers = new ArrayList<>(1);
        InlineKeyboardRow yesNoAnswer = new InlineKeyboardRow(1);
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
        return new InlineKeyboardMarkup(answers);
    }

    private void editAdText(long chatId, long userId, String messageId) {
        EditMessageCaption editMessage = new EditMessageCaption();
        String editedText = String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT,
                advertisementService.getLastUserAdvertisement(chatId, userId).getAdvertisementText()
                .substring(Tag.Actual.getName().length() + 1));
        editMessage.setChatId(chatId);
        editMessage.setMessageId(Integer.parseInt(messageId));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setCaption(editedText);

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot edit deleted message: {}", e.getMessage());
        }
    }

}
