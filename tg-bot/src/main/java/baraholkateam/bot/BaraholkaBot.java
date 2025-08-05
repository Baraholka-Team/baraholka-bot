package baraholkateam.bot;

import baraholkateam.command.BaraholkaBotCommand;
import baraholkateam.command.HelpCommand;
import baraholkateam.command.NonCommand;
import baraholkateam.command.UserAdvertisementsCommand;
import baraholkateam.configuration.BaraholkaBotConfiguration;
import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.notification.NotificationExecutor;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.LastSentMessageDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.dto.StateDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.CommandOrderService;
import baraholkateam.rest.service.CommandService;
import baraholkateam.rest.service.ContactService;
import baraholkateam.rest.service.ContactTypeService;
import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.rest.service.StateService;
import baraholkateam.rest.service.TagService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import baraholkateam.util.Command;
import baraholkateam.util.ContactType;
import baraholkateam.util.Tag;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BaraholkaBot extends CommandLongPollingTelegramBot implements MediaSender, MessageEditor {

    private final TelegramClient telegramClient;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private CommandOrderService commandOrderService;
    @Autowired
    private CommandService commandService;
    @Autowired
    private StateService stateService;
    @Autowired
    private LastSentMessageService lastSentMessageService;
    @Autowired
    private NonCommand nonCommand;
    @Autowired
    private NotificationExecutor notificationExecutor;
    @Autowired
    private TelegramAPIRequests telegramAPIRequests;
    @Autowired
    private HelpCommand helpCommand;
    @Autowired
    private UserAdvertisementsCommand userAdvertisementsCommand;
    @Autowired
    private TagService tagService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactTypeService contactTypeService;

    public BaraholkaBot(BaraholkaBotConfiguration config,
                        TelegramClient telegramClient) {
        super(telegramClient, config.isAllowCommandsWithUsername(), config::getToken);
        this.telegramClient = telegramClient;
    }

    @Override
    public void processInvalidCommandUpdate(Update update) {
        Message message = update.getMessage();
        sendAnswer(message.getChatId(), message.getFrom().getId(), Configuration.CommandMessage.UNKNOWN_COMMAND);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        // Случай получения информации с кнопок (инлайн-клавиатуры) в сообщении
        if (update.hasCallbackQuery()) {
            processInlineKeyboard(update);
            return;
        }

        Message message = update.getMessage();
        if (message == null) {
            log.error(Configuration.ErrorMessage.NO_MESSAGE_FOUND, update.getUpdateId());
            return;
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();

        StateDTO currentState = getCurrentState(chatId, userId);
        if (currentState == null) {
            sendAnswer(chatId, userId, Configuration.ErrorMessage.NO_CURRENT_STATE_FOUND);
            executeCommand(helpCommand, message);
            return;
        } else {
            currentState.setIsFinished(false);
            stateService.addState(currentState);
        }

        // Случай получения информации с кнопок клавиатуры (реплай-клавиатуры) под чатом
        processReplyKeyboard(message, currentState);

        currentState = getCurrentState(chatId, userId);
        if (currentState == null) {
            sendAnswer(chatId, userId, Configuration.ErrorMessage.NO_CURRENT_STATE_FOUND);
            executeCommand(helpCommand, message);
            return;
        }

        if (!currentState.getIsFinished()) {
            // Случай получения информации из введённого пользователем текста
            processUserTextMessage(message, currentState);
        }

        currentState = getCurrentState(chatId, userId);
        if (currentState == null) {
            sendAnswer(chatId, userId, Configuration.ErrorMessage.NO_CURRENT_STATE_FOUND);
            executeCommand(helpCommand, message);
            return;
        }

        if (!currentState.getIsRepeat()) {
            // Случай, когда команда уже обработана, и нет необходимости воспроизводить её заново
            proceedToNextState(currentState.getCurrentCommand(), message);
        }

//        // Случай нажатия на кнопку "Продолжить" во множественном выборе хэштегов
//        if (message.hasText() && Objects.equals(message.getText(), Configuration.Buttons.NEXT_BUTTON)) {
//            addChosenTags(lastSentMessageService.get(chatId));
//
//            if (chosenTagsService.get(chatId) != null
//                    && Command.nextCommand(stateService.get(message.getChatId())) == Command.NewAdvertisement_AddPrice) {
//                List<String> addedTags = advertisementService.getTags(chatId);
//                // Если нужно пропустить добавление цены товара
//                if (!addedTags.contains(Tag.Sale.getName()) && !addedTags.contains(Tag.Bargaining.getName())) {
//                    List<String> tags = chosenTagsService.get(chatId).stream()
//                            .map(Tag::getName)
//                            .toList();
//                    previousStateService.put(chatId, stateService.get(message.getChatId()));
//                    advertisementService.addTags(chatId, tags);
//                    stateService.put(message.getChatId(), Command.NewAdvertisement_AddContacts);
//                    deleteLastMessage(message.getChatId(), userId);
//                    sendAnswer(
//                            chatId,
//                            userId,
//                            String.format(
//                                    Configuration.CommandMessage.CHOSEN_HASHTAGS,
//                                    String.join(" ", advertisementService.getTags(chatId))
//                            )
//                    );
//                    getRegisteredCommand(Command.NewAdvertisement_AddContacts.getName())
//                            .processMessage(this, message, null);
//                    return;
//                }
//            }
//
//            if (chosenTagsService.get(chatId) != null
//                    && (stateService.get(message.getChatId()) == Command.NewAdvertisement_AddAdvertisementTypes
//                    || stateService.get(message.getChatId()) == Command.NewAdvertisement_AddCategories)) {
//                List<String> tags = chosenTagsService.get(chatId).stream()
//                        .map(Tag::getName)
//                        .toList();
//                advertisementService.addTags(chatId, tags);
//                chosenTagsService.delete(chatId);
//            }
//
//            deleteLastMessage(message.getChatId(), userId);
//            Command nextCommand = Command.nextCommand(stateService.get(message.getChatId()));
//            previousStateService.put(chatId, stateService.get(message.getChatId()));
//            stateService.put(chatId, nextCommand);
//            getRegisteredCommand(nextCommand.getName()).processMessage(this, message, null);
//            return;
//        }
//
//        // Обработка введенного пользователем текста
//        executeNonCommand(message, chatId, currentCommand);
    }

    private StateDTO getCurrentState(Long chatId, Long userId) {
        StateDTO currentState = null;
        try {
            currentState = stateService.getState(chatId, userId);
        } catch (BaraholkaBotException e) {
            sendErrorAnswer(chatId, userId, e);
        }
        return currentState;
    }

    private void proceedToNextState(CommandDTO currentCommand, Message message) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        try {
            CommandDTO nextCommand = commandOrderService.getNextCommand(currentCommand);
            executeCommand(nextCommand.getCommand(), message);
        } catch (BaraholkaBotException e) {
            sendErrorAnswer(chatId, userId, e);
        }
    }

    private BaraholkaBotCommand getRegisteredCommand(CommandDTO command) {
        return (BaraholkaBotCommand) getRegisteredCommand(command.getCommand().getName());
    }

    private void processInlineKeyboard(@NotNull Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = (Message) callbackQuery.getMessage();
        String callbackQueryData = callbackQuery.getData();
        parseKeyboardData(callbackQueryData, message);
    }

    private void processReplyKeyboard(@NotNull Message message, @NotNull StateDTO currentState) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        if (message.hasText()) {
            String text = message.getText();
            // Случай нажатия на кнопку "Назад"
            if (Objects.equals(text, Configuration.Buttons.BACK_BUTTON)) {
                Command previousCommand = currentState.getPreviousCommand().getCommand();
                if (previousCommand != null) {
                    Message lastSentMessage = lastSentMessageService.getLastSentMessage(chatId, userId).getMessage();
                    if (lastSentMessage.hasReplyMarkup()) {
                        deleteLastMessage(chatId, userId);
                    }
                    executeCommand(previousCommand, message);
                    return;
                }
            }
            CommandDTO replyCommandDTO = null;
            try {
                replyCommandDTO = commandService.getCommandByDescription(text);
            } catch (BaraholkaBotException e) {
                sendErrorAnswer(chatId, userId, e);
            }
            // Случай выполнения команды по её описанию с кнопки реплай-клавиатуры
            if (replyCommandDTO != null) {
                executeCommand(replyCommandDTO.getCommand(), message);
                return;
            }
            // Случай выполнения действия с кнопки реплай-клавиатуры в рамках текущей команды
            BaraholkaBotCommand currentBotCommand = getRegisteredCommand(currentState.getCurrentCommand());
            currentBotCommand.processReplyKeyboardCommandText(telegramClient, message);
        }
    }

    private void processUserTextMessage(@NotNull Message message, @NotNull StateDTO currentState) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        BaraholkaBotCommand currentBotCommand = getRegisteredCommand(currentState.getCurrentCommand());
        BaraholkaBotCommand.TextProcessResult textProcessResult = currentBotCommand.processUserInput(telegramClient, message);
        if (textProcessResult.messageText() != null) {
            sendAnswer(chatId, userId, textProcessResult.messageText());
        }
        if (textProcessResult.shouldRerunCommand()) {
            sendAnswer(chatId, userId, Configuration.CommandMessage.RERUN_COMMAND);
            executeCommand(currentBotCommand, message);
        } else {
            CommandDTO currentCommandDTO = null;
            try {
                currentCommandDTO = commandService.getCommandByName(currentBotCommand.getCommandIdentifier());
            } catch (BaraholkaBotException e) {
                sendErrorAnswer(chatId, userId, e);
            }
            proceedToNextState(currentCommandDTO, message);
        }
    }

//    private void executeNonCommand(Message msg, Long chatId, Command currCommand) {
//        List<NonCommand.AnswerPair> answers = nonCommand.nonCommandExecute(currCommand);
//        if (answers.get(0).isError()) {
//            for (NonCommand.AnswerPair answer : answers) {
//                SendMessage sendMessage = SendMessage.builder()
//                        .text(answer.answer())
//                        .parseMode(ParseMode.HTML)
//                        .chatId(chatId.toString())
//                        .disableWebPagePreview(true)
//                        .build();
//                try {
//                    telegramClient.execute(sendMessage);
//                } catch (TelegramApiException e) {
//                    log.error("Cannot send message: {}", e.getMessage());
//                }
//            }
//            // ошибка в обработке сообщения пользователя, необходимо повторить данный шаг
//            if (currCommand != null) {
//                getRegisteredCommand(currCommand.getName()).processMessage(this, msg, null);
//            }
//            return;
//        } else {
//            Command nextCommand = CommandService.nextCommand(stateService.get(msg.getChatId()));
//            previousStateService.put(chatId, stateService.get(msg.getChatId()));
//            stateService.put(chatId, nextCommand);
//        }
//        // ошибки в обработке сообщения пользователя нет, отправляем ответ и переходим на следующий шаг
//        for (NonCommand.AnswerPair answer : answers) {
//            if (!answer.isError()) {
//                sendAnswer(chatId, userId, answer.answer());
//            }
//        }
//    }

    private void sendErrorAnswer(@NotNull Long chatId, @NotNull Long userId, @NotNull Exception e) {
        log.error(Configuration.ErrorMessage.SERVER_MESSAGE, e);
        sendAnswer(chatId, userId, Configuration.ErrorMessage.SERVER_MESSAGE, null);
    }

    private void sendAnswer(@NotNull Long chatId, @NotNull Long userId, @NotNull String text) {
        sendAnswer(chatId, userId, text, null);
    }

    private void sendAnswer(@NotNull Long chatId,
                            @NotNull Long userId,
                            @NotNull String text,
                            @Nullable ReplyKeyboard replyKeyboard) {
        SendMessage answer = SendMessage.builder()
                .text(text)
                .parseMode(ParseMode.HTML)
                .chatId(chatId.toString())
                .disableWebPagePreview(true)
                .replyMarkup(replyKeyboard)
                .build();

        try {
            Message sentMessage = telegramClient.execute(answer);
            lastSentMessageService.addLastSentMessage(LastSentMessageDTO.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .message(sentMessage)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Невозможно отправить ответное сообщение:", e);
        }
    }

    private void executeCommand(@NotNull Command command, @NotNull Message message) {
        BaraholkaBotCommand botCommand = (BaraholkaBotCommand) getRegisteredCommand(command.getName());
        executeCommand(botCommand, message, null);
    }

    private void executeCommand(@NotNull BaraholkaBotCommand command, @NotNull Message message) {
        executeCommand(command, message, null);
    }

    private void executeCommand(@NotNull BaraholkaBotCommand command, @NotNull Message message, String[] arguments) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        try {
            CommandDTO commandDTO = commandService.getCommandByName(command.getCommandIdentifier());
            if (commandDTO == null) {
                sendAnswer(chatId, userId, Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(command.getCommandIdentifier()));
            } else {
                command.processMessage(telegramClient, message, arguments);
            }
        } catch (BaraholkaBotException e) {
            sendErrorAnswer(chatId, userId, e);
        }
    }

//    private void addChosenTags(Message message) {
//        List<List<InlineKeyboardButton>> buttons = message.getReplyMarkup().getKeyboard();
//        for (List<InlineKeyboardButton> tag : buttons) {
//            for (InlineKeyboardButton inlineKeyboardButton : tag) {
//                String[] dataCallbackParts = inlineKeyboardButton.getCallbackData().split(" ");
//                if (Objects.equals(dataCallbackParts[dataCallbackParts.length - 1], "1")) {
//                    String newTags = inlineKeyboardButton.getText().split(" ")[1];
//                    List<Tag> tags = chosenTagsService.get(message.getChatId());
//                    if (tags == null || tags.isEmpty()) {
//                        chosenTagsService.put(message.getChatId(),
//                                Arrays.stream(newTags.split(" "))
//                                        .map(Tag::valueOf)
//                                        .toList());
//                    } else {
//                        String chosenTagsString = tags.stream()
//                                .map(Tag::getName)
//                                .collect(Collectors.joining(" "));
//                        chosenTagsService.put(message.getChatId(),
//                                Arrays.stream(String.format("%s %s", chosenTagsString, newTags)
//                                        .split(" "))
//                                        .map(Tag::valueOf)
//                                        .toList());
//                    }
//                }
//            }
//        }
//    }

    private void parseKeyboardData(String callbackQuery, Message message) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        StateDTO currentState;
        try {
            currentState = stateService.getState(chatId, userId);
        } catch (BaraholkaBotException e) {
            sendErrorAnswer(chatId, userId, e);
            return;
        }
        CommandDTO currentCommandDTO = currentState.getCurrentCommand();
        Command currentCommand = currentCommandDTO.getCommand();
        String[] dataParts = callbackQuery.split(" ");

        switch (dataParts[0]) {
            case Configuration.CommandMessage.TAG_CALLBACK_DATA -> {
                if (currentCommand == Command.NewAdvertisement_AddCity) {
                    AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
                    TagDTO newTag = tagService.getTagByName(dataParts[1]);
                    if (newTag == null) {
                        log.error("Тега {} не существует в базе", dataParts[1]);
                    }
                    advertisementDTO.addTag(newTag);
                    advertisementService.saveNewAdvertisement(advertisementDTO);
                } else {
                    chosenTagsService.addChosenTags(
                            chatId,
                            userId,
                            Arrays.stream(dataParts[1].split(" "))
                                    .map(tag -> tagService.getTagByName(tag))
                                    .toList()
                    );
                }

                deleteLastMessage(message.getChatId(), message.getFrom().getId());
                proceedToNextState(currentCommandDTO, message);
            }
            case Configuration.CommandMessage.TAGS_CALLBACK_DATA -> {
                LastSentMessageDTO lastSentMessageDTO = lastSentMessageService.getLastSentMessage(chatId, userId);
                Message lastSentMessage = lastSentMessageDTO.getMessage();
                List<InlineKeyboardRow> buttons = lastSentMessage.getReplyMarkup().getKeyboard();
                InlineKeyboardButton changeTag = getInlineKeyboardButton(buttons, dataParts);
                buttons.get(Integer.parseInt(dataParts[2])).remove(Integer.parseInt(dataParts[3]));
                buttons.get(Integer.parseInt(dataParts[2])).add(Integer.parseInt(dataParts[3]), changeTag);
                InlineKeyboardMarkup ikm = new InlineKeyboardMarkup(buttons);
                lastSentMessage.setReplyMarkup(ikm);
                lastSentMessageService.addLastSentMessage(lastSentMessageDTO);
                editMessageReplyMarkup(message.getChatId(), message.getFrom().getId(), buttons);
            }
            case Configuration.CommandMessage.PHONE_CALLBACK_DATA -> {
                deleteLastMessage(message.getChatId(), message.getFrom().getId());
                if (Objects.equals(dataParts[1], "yes")) {
                    CommandDTO commandDTO;
                    try {
                        commandDTO = commandService.getCommandByName(Command.NewAdvertisement_AddPhone.getName());
                        if (commandDTO == null) {
                            sendAnswer(
                                    chatId,
                                    userId,
                                    Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(Command.NewAdvertisement_AddPhone.getName())
                            );
                        } else {
                            proceedToNextState(commandDTO, message);
                        }
                    } catch (BaraholkaBotException e) {
                       sendErrorAnswer(chatId, userId, e);
                    }
                } else if (Objects.equals(dataParts[1], "no")) {
                    CommandDTO commandDTO;
                    try {
                        commandDTO = commandService.getCommandByName(Command.NewAdvertisement_ConfirmPhone.getName());
                        if (commandDTO == null) {
                            sendAnswer(
                                    chatId,
                                    userId,
                                    Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(Command.NewAdvertisement_ConfirmPhone.getName())
                            );
                        } else {
                            proceedToNextState(commandDTO, message);
                        }
                    } catch (BaraholkaBotException e) {
                        sendErrorAnswer(chatId, userId, e);
                    }
                }
            }
            case Configuration.CommandMessage.SOCIAL_CALLBACK_DATA -> {
                deleteLastMessage(message.getChatId(), message.getFrom().getId());
                if (Objects.equals(dataParts[1], "yes")) {
                    CommandDTO commandDTO;
                    try {
                        commandDTO = commandService.getCommandByName(Command.NewAdvertisement_AddSocial.getName());
                        if (commandDTO == null) {
                            sendAnswer(
                                    chatId,
                                    userId,
                                    Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(Command.NewAdvertisement_AddSocial.getName())
                            );
                        } else {
                            proceedToNextState(commandDTO, message);
                        }
                    } catch (BaraholkaBotException e) {
                        sendErrorAnswer(chatId, userId, e);
                    }
                } else if (Objects.equals(dataParts[1], "no")) {
                    CommandDTO commandDTO;
                    try {
                        commandDTO = commandService.getCommandByName(Command.NewAdvertisement_Confirm.getName());
                        if (commandDTO == null) {
                            sendAnswer(
                                    chatId,
                                    userId,
                                    Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(Command.NewAdvertisement_Confirm.getName())
                            );
                        } else {
                            proceedToNextState(commandDTO, message);
                        }
                    } catch (BaraholkaBotException e) {
                        sendErrorAnswer(chatId, userId, e);
                    }
                }
            }
            case Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA -> {
                deleteLastMessage(message.getChatId(), message.getFrom().getId());
                if (Objects.equals(dataParts[1], "yes")) {
                    AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
                    if (advertisementDTO.getContacts().isEmpty()) {
                        contactService.addContact(
                                ContactDTO.builder()
                                        .contactType(contactTypeService.getContactTypeByName(ContactType.Social.getName()))
                                        .contactName("@" + telegramAPIRequests.getUser(chatId, userId).username())
                                        .build()
                        );
                    }

                    Message sentAd;
                    if (Objects.equals(dataParts[2], "0")) {
                        sentAd = sendPhotoMessage(
                                message.getChatId(),
                                PhotoConverter.convertBase64StringToPhoto(advertisementDTO.getPhotos().get(0).getPhoto()),
                                advertisementDTO.getAdvertisementText()
                        );
                    } else {
                        List<File> photoFiles = new ArrayList<>();
                        for (PhotoDTO photo : advertisementDTO.getPhotos()) {
                            photoFiles.add(Objects.requireNonNull(PhotoConverter.convertBase64StringToPhoto(photo.getPhoto())));
                        }
                        sentAd = sendPhotoMediaGroup(
                                message.getChatId(),
                                photoFiles,
                                advertisementDTO.getAdvertisementText()
                        ).get(0);
                    }
                    if (sentAd != null) {
                        advertisementDTO
                                .setMessageId(sentAd.getMessageId())
//                                .setCreationTime(System.currentTimeMillis())
//                                .setNextUpdateTime(
//                                        System.currentTimeMillis()
//                                                + FIRST_REPEAT_NOTIFICATION_TIME_UNIT
//                                                .toMillis(FIRST_REPEAT_NOTIFICATION_PERIOD)
//                                )
//                                .setUpdateAttempt(0)
                        ;
                        advertisementService.saveNewAdvertisement(advertisementDTO);
                        sendAnswer(chatId, userId, Configuration.CommandMessage.SUCCESS_TEXT);
                    } else {
                        sendAnswer(chatId, userId, Configuration.CommandMessage.UNSUCCESS_TEXT);
                        log.error("Error while sending advertisement to channel.");
                    }
                } else if (Objects.equals(dataParts[1], "no")) {
                    sendAnswer(userId, chatId, Configuration.CommandMessage.ADVERTISEMENT_CANCELLED_TEXT);
                }
                CommandDTO commandDTO;
                try {
                    commandDTO = commandService.getCommandByName(Command.MainMenu.getName());
                    if (commandDTO == null) {
                        sendAnswer(
                                chatId,
                                userId,
                                Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(Command.MainMenu.getName())
                        );
                    } else {
                        proceedToNextState(commandDTO, message);
                    }
                } catch (BaraholkaBotException e) {
                    sendErrorAnswer(chatId, userId, e);
                }
            }
            case Configuration.CommandMessage.NOTIFICATION_CALLBACK_DATA -> {
//                long chatId = Long.parseLong(dataParts[1]);
                int messageId = Integer.parseInt(dataParts[2]);

                if (Objects.equals(dataParts[4], "0")) {
                    String advertisementText = advertisementService.getLastUserAdvertisement(chatId, userId)
                            .getAdvertisementText()
                            .substring(Tag.Actual.getName().length() + 1);
                    editAdvertisementText(
                            chatId,
                            Long.parseLong(dataParts[2]),
                            String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT, advertisementText)
                    );
                    advertisementService.removeAdvertisement(chatId, messageId);
                    sendAnswer(chatId, userId, Configuration.CommandMessage.ADVERTISEMENT_SUCCESSFUL_DELETE);
                } else {
//                    advertisementService.setNextUpdateTime(messageId,
//                            System.currentTimeMillis() + Long.parseLong(dataParts[3]));
//                    advertisementService.setUpdateAttempt(messageId, 0);
                    sendAnswer(chatId, userId, Configuration.CommandMessage.ADVERTISEMENT_SUCCESSFUL_UPDATE);
                }
                notificationExecutor.deleteMessages(chatId, messageId);
            }
            case Configuration.CommandMessage.DELETE_CALLBACK_TEXT -> {
                int messageId = Integer.parseInt(dataParts[1]);

                deleteLastMessage(message.getChatId(), message.getFrom().getId());
                telegramAPIRequests.forwardMessage(String.valueOf(chatId), String.valueOf(message.getChatId()), messageId);
                sendAnswer(chatId, userId, Configuration.CommandMessage.DELETE_AD, getDeleteAd(messageId));
            }
            case Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT -> {
                deleteLastMessage(message.getChatId(), message.getFrom().getId());
                if (Objects.equals(dataParts[1], "1")) {
                    String advertisementText = advertisementService.getLastUserAdvertisement(chatId, userId)
                            .getAdvertisementText()
                            .substring(Tag.Actual.getName().length() + 1);
                    editAdvertisementText(
                            message.getChatId(),
                            Long.parseLong(dataParts[2]),
                            String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT, advertisementText)
                    );
                    advertisementService.removeAdvertisement(chatId, Integer.parseInt(dataParts[2]));
                    sendAnswer(chatId, userId, Configuration.CommandMessage.SUCCESS_DELETE_AD_TEXT);
                } else {
                    sendAnswer(chatId, userId, Configuration.CommandMessage.UNSUCCESS_DELETE_AD_TEXT);
                    CommandDTO commandDTO;
                    try {
                        commandDTO = commandService.getCommandByName(Command.DeleteAdvertisement.getName());
                        if (commandDTO == null) {
                            sendAnswer(
                                    chatId,
                                    userId,
                                    Configuration.ErrorMessage.NO_COMMAND_WITH_NAME.formatted(Command.DeleteAdvertisement.getName())
                            );
                        } else {
                            proceedToNextState(commandDTO, message);
                        }
                    } catch (BaraholkaBotException e) {
                        sendErrorAnswer(chatId, userId, e);
                    }
                }
            }
            default -> log.error("Unknown command in callback data: {}", callbackQuery);
        }
    }

    private static InlineKeyboardButton getInlineKeyboardButton(List<InlineKeyboardRow> buttons, String[] dataParts) {
        InlineKeyboardButton changeTag = buttons.get(Integer.parseInt(dataParts[2]))
                .get(Integer.parseInt(dataParts[3]));
        if (Objects.equals(dataParts[4], "0")) {
            changeTag.setText(String.format(Configuration.CommandMessage.CHOSEN_TAG, changeTag.getText().split(" ")[1]));
            changeTag.setCallbackData(changeTag
                    .getCallbackData()
                    .substring(0, changeTag.getCallbackData().length() - 2)
                    .concat(" 1"));
        } else {
            changeTag.setText(String.format(Configuration.CommandMessage.NOT_CHOSEN_TAG, changeTag.getText().split(" ")[1]));
            changeTag.setCallbackData(changeTag
                    .getCallbackData()
                    .substring(0, changeTag.getCallbackData().length() - 2)
                    .concat(" 0"));
        }
        return changeTag;
    }

    @Override
    public void editAdvertisementText(Long chatId, Long userId, String text) {
        EditMessageCaption editMessage = new EditMessageCaption();
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
        String adText = advertisementDTO.getAdvertisementText().substring(Tag.Actual.getName().length() + 1);
        String editedText = text.formatted(adText);
        editMessage.setChatId(chatId);
        editMessage.setMessageId(advertisementDTO.getMessageId());
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setCaption(editedText);

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot edit deleted message: {}", e.getMessage());
        }
    }

    @Override
    public void deleteLastMessage(Long chatId, Long userId) {
        int messageId = lastSentMessageService.getLastSentMessage(chatId, userId).getMessage().getMessageId();
        DeleteMessage deleteLastMessage = new DeleteMessage(String.valueOf(chatId), messageId);
        try {
            telegramClient.execute(deleteLastMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot delete last message due to: {}", e.getMessage());
        }
    }

    @Override
    public void editMessageReplyMarkup(Long chatId, Long userId, List<InlineKeyboardRow> buttons) {
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup(buttons);
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(lastSentMessageService.getLastSentMessage(chatId, userId).getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(ikm);
        try {
            telegramClient.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Cannot edit message reply markup due to: {}", e.getMessage());
        }
    }

    private InlineKeyboardMarkup getDeleteAd(Integer messageId) {
        InlineKeyboardRow buttons = new InlineKeyboardRow(2);

        InlineKeyboardButton yes = new InlineKeyboardButton("Да");
        yes.setCallbackData(String.format("%s 1 %d", Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT, messageId));
        buttons.add(yes);

        InlineKeyboardButton no = new InlineKeyboardButton("Нет");
        no.setCallbackData(String.format("%s 0", Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT));
        buttons.add(no);

        List<InlineKeyboardRow> rowList = new ArrayList<>();
        rowList.add(buttons);

        return new InlineKeyboardMarkup(rowList);
    }

//    @Override
//    public File downloadFileByFilePath(String filePath) {
//        try {
//            return telegramClient.downloadFile(filePath);
//        } catch (TelegramApiException e) {
//            log.error("Cannot download file {}", filePath, e);
//            return null;
//        }
//    }

    @Override
    public Message sendPhotoMessage(long chatId, File photoFile, String text) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(Objects.requireNonNull(photoFile)))
                .caption(text)
                .parseMode(ParseMode.HTML)
                .build();

        try {
            return telegramClient.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Can't send photo message", e);
            return null;
        }
    }

    @Override
    public List<Message> sendPhotoMediaGroup(long chatId, List<File> photoFiles, String text) {
        AtomicInteger count = new AtomicInteger();
        List<InputMedia> medias = photoFiles.stream()
                .map(photoFile -> {
                    String mediaName = UUID.randomUUID().toString();

                    InputMediaPhoto inputMediaPhoto = InputMediaPhoto.builder()
                            .media(photoFile, mediaName)
                            .build();

                    inputMediaPhoto.setParseMode(ParseMode.HTML);

                    if (count.getAndIncrement() == 0) {
                        inputMediaPhoto.setCaption(text);
                    }

                    return inputMediaPhoto;
                })
                .collect(Collectors.toList());

        SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(medias)
                .build();

        try {
            return telegramClient.execute(sendMediaGroup);
        } catch (TelegramApiException e) {
            log.error("Can't send photos with media group", e);
            return null;
        }
    }

}
