package baraholkateam.bot;

import baraholkateam.command.BaraholkaBotCommand;
import baraholkateam.command.DeleteAdvertisementCommand;
import baraholkateam.command.HelpCommand;
import baraholkateam.command.MainMenuCommand;
import baraholkateam.command.NewAdvertisementAddAdvertisementTypesCommand;
import baraholkateam.command.NewAdvertisementAddCategoriesCommand;
import baraholkateam.command.NewAdvertisementAddCityCommand;
import baraholkateam.command.NewAdvertisementAddContactsCommand;
import baraholkateam.command.NewAdvertisementAddDescriptionCommand;
import baraholkateam.command.NewAdvertisementAddPhoneCommand;
import baraholkateam.command.NewAdvertisementAddPhotosCommand;
import baraholkateam.command.NewAdvertisementAddPriceCommand;
import baraholkateam.command.NewAdvertisementAddSocialCommand;
import baraholkateam.command.NewAdvertisementCommand;
import baraholkateam.command.NewAdvertisementConfirmCommand;
import baraholkateam.command.NewAdvertisementConfirmPhoneCommand;
import baraholkateam.command.NewAdvertisementConfirmPhotoCommand;
import baraholkateam.command.NewAdvertisementConfirmPriceCommand;
import baraholkateam.command.NonCommand;
import baraholkateam.command.SearchAdvertisementsCommand;
import baraholkateam.command.SearchAdvertisementsAddAdvertisementTypesCommand;
import baraholkateam.command.SearchAdvertisementsAddProductCategoriesCommand;
import baraholkateam.command.SearchAdvertisementsShowFoundAdvertisementsCommand;
import baraholkateam.command.StartCommand;
import baraholkateam.command.UserAdvertisementsCommand;
import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.notification.NotificationExecutor;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.dto.StateDTO;
import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.CommandService;
import baraholkateam.rest.service.StateService;
import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.PhotoConverter;
import baraholkateam.util.Command;
import baraholkateam.util.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static baraholkateam.notification.NotificationExecutor.FIRST_REPEAT_NOTIFICATION_PERIOD;
import static baraholkateam.notification.NotificationExecutor.FIRST_REPEAT_NOTIFICATION_TIME_UNIT;

@Slf4j
@Component
public class BaraholkaBot extends TelegramLongPollingCommandBot implements FileLoader, MediaSender, MessageEditor {

    private final String botName;
    @Value("${channel.chat_id}")
    private String channelChatId;
    @Value("${channel.username}")
    private String channelUsername;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ChosenTagsService chosenTagsService;
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
    private StartCommand startCommand;
    @Autowired
    private HelpCommand helpCommand;
    @Autowired
    private MainMenuCommand mainMenuCommand;
    @Autowired
    private UserAdvertisementsCommand userAdvertisementsCommand;
    @Autowired
    private DeleteAdvertisementCommand deleteAdvertisementCommand;
    @Autowired
    private NewAdvertisementAddPhotosCommand newAdvertisementAddPhotosCommand;
    @Autowired
    private NewAdvertisementConfirmPhotoCommand newAdvertisementConfirmPhotoCommand;
    @Autowired
    private NewAdvertisementAddDescriptionCommand newAdvertisementAddDescriptionCommand;
    @Autowired
    private NewAdvertisementAddCityCommand newAdvertisementAddCityCommand;
    @Autowired
    private NewAdvertisementAddAdvertisementTypesCommand newAdvertisementAddAdvertisementTypesCommand;
    @Autowired
    private NewAdvertisementAddCategoriesCommand newAdvertisementAddCategoriesCommand;
    @Autowired
    private NewAdvertisementAddPriceCommand newAdvertisementAddPriceCommand;
    @Autowired
    private NewAdvertisementConfirmPriceCommand newAdvertisementConfirmPriceCommand;
    @Autowired
    private NewAdvertisementCommand newAdvertisementCommand;
    @Autowired
    private NewAdvertisementAddContactsCommand newAdvertisementAddContactsCommand;
    @Autowired
    private NewAdvertisementAddPhoneCommand newAdvertisementAddPhoneCommand;
    @Autowired
    private NewAdvertisementConfirmPhoneCommand newAdvertisementConfirmPhoneCommand;
    @Autowired
    private NewAdvertisementAddSocialCommand newAdvertisementAddSocialCommand;
    @Autowired
    private NewAdvertisementConfirmCommand newAdvertisementConfirmCommand;
    @Autowired
    private SearchAdvertisementsCommand searchAdvertisementsCommand;
    @Autowired
    private SearchAdvertisementsAddAdvertisementTypesCommand searchAdvertisementsAddAdvertisementTypesCommand;
    @Autowired
    private SearchAdvertisementsAddProductCategoriesCommand searchAdvertisementsAddProductCategoriesCommand;
    @Autowired
    private SearchAdvertisementsShowFoundAdvertisementsCommand searchAdvertisementsShowFoundAdvertisementsCommand;

    public BaraholkaBot(
            @Value("${bot.name}") String botName,
            @Value("${bot.token}") String botToken
    ) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onRegister() {
        super.onRegister();

        List<BaraholkaBotCommand> commandList = List.of(
                startCommand,
                helpCommand,
                mainMenuCommand,
                userAdvertisementsCommand,
                deleteAdvertisementCommand,
                newAdvertisementCommand,
                newAdvertisementAddPhotosCommand,
                newAdvertisementConfirmPhotoCommand,
                newAdvertisementAddDescriptionCommand,
                newAdvertisementAddCityCommand,
                newAdvertisementAddAdvertisementTypesCommand,
                newAdvertisementAddCategoriesCommand,
                newAdvertisementAddPriceCommand,
                newAdvertisementConfirmPriceCommand,
                newAdvertisementAddContactsCommand,
                newAdvertisementAddPhoneCommand,
                newAdvertisementConfirmPhoneCommand,
                newAdvertisementAddSocialCommand,
                newAdvertisementConfirmCommand,
                searchAdvertisementsCommand,
                searchAdvertisementsAddAdvertisementTypesCommand,
                searchAdvertisementsAddProductCategoriesCommand,
                searchAdvertisementsShowFoundAdvertisementsCommand
        );
        registerAll(commandList.toArray(BaraholkaBotCommand[]::new));
    }

    @Override
    public void processInvalidCommandUpdate(Update update) {
        sendAnswer(update.getMessage().getChatId(), Configuration.CommandMessage.UNKNOWN_COMMAND);
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
        StateDTO currentState;
        try {
            currentState = stateService.getState(chatId, userId);
        } catch (BaraholkaBotException e) {
            sendErrorAnswer(chatId, e);
            executeCommand(helpCommand, message);
            return;
        }

        Command currentCommand = currentState.getCurrentCommand().getCommand();
        BaraholkaBotCommand currentBotCommand = (BaraholkaBotCommand) getRegisteredCommand(currentCommand.getName());
        if (currentBotCommand == null) {
            sendAnswer(chatId, Configuration.ErrorMessage.NO_CURRENT_STATE_FOUND.formatted(userId));
            executeCommand(helpCommand, message);
            return;
        }

        // Случай получения информации с кнопок клавиатуры (реплай-клавиатуры) под чатом
        processReplyKeyboard(message, currentBotCommand, currentState);

        // Случай получения информации из введённого пользователем текста
        processUserTextMessage(message, currentBotCommand, chatId);

        // Случай удаления всех фотографий по кнопке
        if (message.hasText() && Objects.equals(message.getText(), Configuration.CommandMessage.DELETE_ALL_PHOTOS)
                && stateService.get(chatId) == Command.NewAdvertisement_ConfirmPhoto) {
            advertisementService.setPhotos(message.getChatId(), new ArrayList<>());
            sendAnswer(chatId, Configuration.CommandMessage.PHOTOS_DELETE);
            stateService.put(chatId, Command.NewAdvertisement_AddPhotos);
            getRegisteredCommand(Command.NewAdvertisement_AddPhotos.getName()).processMessage(this, message, null);
            return;
        }

        // Случай удаления всех социальных сетей по кнопке
        if (message.hasText() && Objects.equals(message.getText(), Configuration.CommandMessage.DELETE_ALL_SOCIALS)
                && stateService.get(chatId) == Command.NewAdvertisement_ConfirmPhone) {
            advertisementService.addContacts(message.getChatId(), new ArrayList<>());
            sendAnswer(chatId, Configuration.CommandMessage.SOCIALS_DELETE);
            stateService.put(chatId, Command.NewAdvertisement_ConfirmPhone);
            getRegisteredCommand(Command.NewAdvertisement_ConfirmPhone.getName()).processMessage(this, message, null);
            return;
        }

        // Случай нажатия на кнопку "Продолжить" во множественном выборе хэштегов
        if (message.hasText() && Objects.equals(message.getText(), Configuration.Buttons.NEXT_BUTTON)) {
            addChosenTags(lastSentMessageService.get(chatId));

            if (chosenTagsService.get(chatId) != null
                    && Command.nextCommand(stateService.get(message.getChatId())) == Command.NewAdvertisement_AddPrice) {
                List<String> addedTags = advertisementService.getTags(chatId);
                // Если нужно пропустить добавление цены товара
                if (!addedTags.contains(Tag.Sale.getName()) && !addedTags.contains(Tag.Bargaining.getName())) {
                    List<String> tags = chosenTagsService.get(chatId).stream()
                            .map(Tag::getName)
                            .toList();
                    previousStateService.put(chatId, stateService.get(message.getChatId()));
                    advertisementService.addTags(chatId, tags);
                    stateService.put(message.getChatId(), Command.NewAdvertisement_AddContacts);
                    deleteLastMessage(message.getChatId(), userId);
                    sendAnswer(
                            chatId,
                            String.format(
                                    Configuration.CommandMessage.CHOSEN_HASHTAGS,
                                    String.join(" ", advertisementService.getTags(chatId))
                            )
                    );
                    getRegisteredCommand(Command.NewAdvertisement_AddContacts.getName())
                            .processMessage(this, message, null);
                    return;
                }
            }

            if (chosenTagsService.get(chatId) != null
                    && (stateService.get(message.getChatId()) == Command.NewAdvertisement_AddAdvertisementTypes
                    || stateService.get(message.getChatId()) == Command.NewAdvertisement_AddCategories)) {
                List<String> tags = chosenTagsService.get(chatId).stream()
                        .map(Tag::getName)
                        .toList();
                advertisementService.addTags(chatId, tags);
                chosenTagsService.delete(chatId);
            }

            deleteLastMessage(message.getChatId(), userId);
            Command nextCommand = Command.nextCommand(stateService.get(message.getChatId()));
            previousStateService.put(chatId, stateService.get(message.getChatId()));
            stateService.put(chatId, nextCommand);
            getRegisteredCommand(nextCommand.getName()).processMessage(this, message, null);
            return;
        }

        // Обработка введенного пользователем текста
        executeNonCommand(message, chatId, currentCommand);
    }

    private void processInlineKeyboard(@NotNull Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = (Message) callbackQuery.getMessage();
        String callbackQueryData = callbackQuery.getData();
        parseKeyboardData(callbackQueryData, message);
    }

    private void processReplyKeyboard(@NotNull Message message,
                                      @NotNull BaraholkaBotCommand currentBotCommand,
                                      @NotNull StateDTO currentState) {
        Long chatId = currentState.getChatId();
        Long userId = currentState.getUserId();
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
            CommandDTO replyCommandDTO;
            try {
                replyCommandDTO = commandService.getCommandByDescription(text);
            } catch (BaraholkaBotException e) {
                sendErrorAnswer(chatId, e);
                return;
            }
            // Случай выполнения команды по её описанию с кнопки реплай-клавиатуры
            if (replyCommandDTO != null) {
                executeCommand(replyCommandDTO.getCommand(), message);
                return;
            }
            // Случай выполнения действия с кнопки реплай-клавиатуры в рамках текущей команды
            currentBotCommand.processReplyKeyboardCommandText(message);
        }
    }

    private void processUserTextMessage(@NotNull Message message,
                                        @NotNull BaraholkaBotCommand currentBotCommand,
                                        @NotNull Long chatId) {
        BaraholkaBotCommand.TextProcessResult textProcessResult = currentBotCommand.processUserInput(this, message);
        if (!textProcessResult.messageText().isBlank()) {
            sendAnswer(chatId, textProcessResult.messageText());
        }
        if (textProcessResult.shouldRerunCommand()) {
            sendAnswer(chatId, Configuration.CommandMessage.RERUN_COMMAND);
            executeCommand(currentBotCommand, message);
        } else {
            try {
                Command currentCommand = CommandDTO.findCommand(currentBotCommand.getCommandIdentifier());
                Command nextCommand = CommandDTO.nextCommand(currentCommand);
                executeCommand(nextCommand, message);
            } catch (BaraholkaBotException e) {
                sendErrorAnswer(chatId, e);
                executeCommand(helpCommand, message);
            }
        }
    }

    private void executeNonCommand(Message msg, Long chatId, Command currCommand) {
        List<NonCommand.AnswerPair> answers = nonCommand.nonCommandExecute(currCommand);
        if (answers.get(0).isError()) {
            for (NonCommand.AnswerPair answer : answers) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(answer.answer());
                sendMessage.setParseMode(ParseMode.HTML);
                sendMessage.setChatId(chatId.toString());
                sendMessage.disableWebPagePreview();
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    log.error("Cannot send message: {}", e.getMessage());
                }
            }
            // ошибка в обработке сообщения пользователя, необходимо повторить данный шаг
            if (currCommand != null) {
                getRegisteredCommand(currCommand.getName()).processMessage(this, msg, null);
            }
            return;
        } else {
            Command nextCommand = Command.nextCommand(stateService.get(msg.getChatId()));
            previousStateService.put(chatId, stateService.get(msg.getChatId()));
            stateService.put(chatId, nextCommand);
        }
        // ошибки в обработке сообщения пользователя нет, отправляем ответ и переходим на следующий шаг
        for (NonCommand.AnswerPair answer : answers) {
            if (!answer.isError()) {
                sendAnswer(chatId, answer.answer());
            }
        }
    }

    private void sendAnswer(@NotNull Long chatId, @NotNull String text) {
        sendAnswer(chatId, text, null);
    }

    private void sendErrorAnswer(@NotNull Long chatId, @NotNull Exception e) {
        sendAnswer(chatId, Configuration.ErrorMessage.SERVER_MESSAGE.formatted(e.getMessage()), null);
    }

    private void sendAnswer(@NotNull Long chatId,
                            @NotNull String text,
                            @Nullable ReplyKeyboard replyKeyboard) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setParseMode(ParseMode.HTML);
        answer.setChatId(chatId.toString());
        if (replyKeyboard != null) {
            answer.setReplyMarkup(replyKeyboard);
        }
        answer.disableWebPagePreview();

        try {
            Message sentMessage = execute(answer);
            lastSentMessageService.put(chatId, sentMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot execute command: {}", e.getMessage());
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
        command.processMessage(this, message, arguments);
    }

    private void addChosenTags(Message message) {
        List<List<InlineKeyboardButton>> buttons = message.getReplyMarkup().getKeyboard();
        for (List<InlineKeyboardButton> tag : buttons) {
            for (InlineKeyboardButton inlineKeyboardButton : tag) {
                String[] dataCallbackParts = inlineKeyboardButton.getCallbackData().split(" ");
                if (Objects.equals(dataCallbackParts[dataCallbackParts.length - 1], "1")) {
                    String newTags = inlineKeyboardButton.getText().split(" ")[1];
                    List<Tag> tags = chosenTagsService.get(message.getChatId());
                    if (tags == null || tags.isEmpty()) {
                        chosenTagsService.put(message.getChatId(),
                                Arrays.stream(newTags.split(" "))
                                        .map(Tag::valueOf)
                                        .toList());
                    } else {
                        String chosenTagsString = tags.stream()
                                .map(Tag::getName)
                                .collect(Collectors.joining(" "));
                        chosenTagsService.put(message.getChatId(),
                                Arrays.stream(String.format("%s %s", chosenTagsString, newTags)
                                        .split(" "))
                                        .map(Tag::valueOf)
                                        .toList());
                    }
                }
            }
        }
    }

    private void parseKeyboardData(String callbackQuery, Message msg) {
        String[] dataParts = callbackQuery.split(" ");
        switch (dataParts[0]) {
            case Configuration.CommandMessage.TAG_CALLBACK_DATA -> {
                if (stateService.get(msg.getChatId()) == Command.NewAdvertisement_AddCity) {
                    advertisementService.addTag(msg.getChatId(), dataParts[1]);
                } else {
                    List<Tag> tags = chosenTagsService.get(msg.getChatId());
                    if (tags == null || tags.isEmpty()) {
                        chosenTagsService.put(msg.getChatId(),
                                Arrays.stream(dataParts[1].split(" "))
                                        .map(Tag::valueOf)
                                        .toList());
                    } else {
                        String chosenTagsString = tags.stream()
                                .map(Tag::getName)
                                .collect(Collectors.joining(" "));
                        chosenTagsService.put(msg.getChatId(),
                                Arrays.stream(String.format("%s %s", chosenTagsString, dataParts[1]).split(" "))
                                        .map(Tag::valueOf)
                                        .toList());
                    }
                }

                deleteLastMessage(msg.getChatId(), msg.getFrom().getId());
                Command nextCommand = Command.nextCommand(stateService.get(msg.getChatId()));
                previousStateService.put(msg.getChatId(), stateService.get(msg.getChatId()));
                stateService.put(msg.getChatId(), nextCommand);
                getRegisteredCommand(nextCommand.getName()).processMessage(this, msg, null);
            }
            case Configuration.CommandMessage.TAGS_CALLBACK_DATA -> {
                Message lastSentMessage = lastSentMessageService.get(msg.getChatId());
                List<List<InlineKeyboardButton>> buttons = lastSentMessage.getReplyMarkup().getKeyboard();
                InlineKeyboardButton changeTag = getInlineKeyboardButton(buttons, dataParts);
                buttons.get(Integer.parseInt(dataParts[2])).remove(Integer.parseInt(dataParts[3]));
                buttons.get(Integer.parseInt(dataParts[2])).add(Integer.parseInt(dataParts[3]), changeTag);
                InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
                ikm.setKeyboard(buttons);
                lastSentMessage.setReplyMarkup(ikm);
                lastSentMessageService.put(msg.getChatId(), lastSentMessage);
                editMessageReplyMarkup(msg.getChatId(), buttons);
            }
            case Configuration.CommandMessage.PHONE_CALLBACK_DATA -> {
                deleteLastMessage(msg.getChatId(), msg.getFrom().getId());
                if (Objects.equals(dataParts[1], "yes")) {
                    stateService.put(msg.getChatId(), Command.NewAdvertisement_AddPhone);
                    getRegisteredCommand(Command.NewAdvertisement_AddPhone.getName())
                            .processMessage(this, msg, null);
                } else if (Objects.equals(dataParts[1], "no")) {
                    stateService.put(msg.getChatId(), Command.NewAdvertisement_ConfirmPhone);
                    getRegisteredCommand(Command.NewAdvertisement_ConfirmPhone.getName())
                            .processMessage(this, msg, null);
                }
            }
            case Configuration.CommandMessage.SOCIAL_CALLBACK_DATA -> {
                deleteLastMessage(msg.getChatId(), msg.getFrom().getId());
                if (Objects.equals(dataParts[1], "yes")) {
                    stateService.put(msg.getChatId(), Command.NewAdvertisement_AddSocial);
                    getRegisteredCommand(Command.NewAdvertisement_AddSocial.getName())
                            .processMessage(this, msg, null);
                } else if (Objects.equals(dataParts[1], "no")) {
                    stateService.put(msg.getChatId(), Command.NewAdvertisement_Confirm);
                    getRegisteredCommand(Command.NewAdvertisement_Confirm.getName())
                            .processMessage(this, msg, null);
                }
            }
            case Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA -> {
                deleteLastMessage(msg.getChatId(), msg.getFrom().getId());
                if (Objects.equals(dataParts[1], "yes")) {
                    if (advertisementService.getContacts(msg.getChatId()).isEmpty()
                            && advertisementService.getPhone(msg.getChatId()) == null) {
                        advertisementService.addContacts(msg.getChatId(),
                                List.of("@" + telegramAPIRequests.getUser(msg.getChatId()).username()));
                    }

                    Message sentAd;
                    if (Objects.equals(dataParts[2], "0")) {
                        sentAd = sendPhotoMessage(
                                Long.parseLong(channelChatId),
                                PhotoConverter.convertBase64StringToPhoto(
                                        advertisementService.getPhotos(msg.getChatId()).get(0)
                                ),
                                advertisementService.getAdvertisementText(msg.getChatId())
                        );
                    } else {
                        List<File> photoFiles = new ArrayList<>();
                        for (String photo : advertisementService.getPhotos(msg.getChatId())) {
                            photoFiles.add(Objects.requireNonNull(PhotoConverter.convertBase64StringToPhoto(photo)));
                        }
                        sentAd = sendPhotoMediaGroup(
                                Long.parseLong(channelChatId),
                                photoFiles,
                                advertisementService.getAdvertisementText(msg.getChatId())
                        ).get(0);
                    }
                    if (sentAd != null) {
                        AdvertisementEntity currentAdvertisementEntity = advertisementService.get(msg.getChatId());
                        currentAdvertisementEntity
                                .setMessageId(sentAd.getMessageId())
                                .setCreationTime(System.currentTimeMillis())
                                .setNextUpdateTime(
                                        System.currentTimeMillis()
                                                + FIRST_REPEAT_NOTIFICATION_TIME_UNIT
                                                .toMillis(FIRST_REPEAT_NOTIFICATION_PERIOD)
                                )
                                .setUpdateAttempt(0);
                        advertisementService.put(currentAdvertisementEntity);
                        advertisementService.saveNewAdvertisement(currentAdvertisementEntity);
                        sendAnswer(msg.getChatId(), Configuration.CommandMessage.SUCCESS_TEXT);
                    } else {
                        sendAnswer(msg.getChatId(), Configuration.CommandMessage.UNSUCCESS_TEXT);
                        log.error("Error while sending advertisement to channel.");
                    }
                } else if (Objects.equals(dataParts[1], "no")) {
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.ADVERTISEMENT_CANCELLED_TEXT);
                }
                stateService.put(msg.getChatId(), Command.MainMenu);
                getRegisteredCommand(Command.MainMenu.getName())
                        .processMessage(this, msg, null);
            }
            case Configuration.CommandMessage.NOTIFICATION_CALLBACK_DATA -> {
                long chatId = Long.parseLong(dataParts[1]);
                int messageId = Integer.parseInt(dataParts[2]);

                if (Objects.equals(dataParts[4], "0")) {
                    String advertisementText = advertisementService.getAdvertisement(chatId, messageId)
                            .getAdvertisementText()
                            .substring(Tag.Actual.getName().length() + 1);
                    editAdvertisementText(
                            chatId,
                            Integer.parseInt(dataParts[2]),
                            String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT, advertisementText)
                    );
                    advertisementService.removeAdvertisement(messageId);
                    sendAnswer(chatId, Configuration.CommandMessage.ADVERTISEMENT_SUCCESSFUL_DELETE);
                } else {
                    advertisementService.setNextUpdateTime(messageId,
                            System.currentTimeMillis() + Long.parseLong(dataParts[3]));
                    advertisementService.setUpdateAttempt(messageId, 0);
                    sendAnswer(chatId, Configuration.CommandMessage.ADVERTISEMENT_SUCCESSFUL_UPDATE);
                }
                notificationExecutor.deleteMessages(this, chatId, messageId);
            }
            case Configuration.CommandMessage.DELETE_CALLBACK_TEXT -> {
                long messageId = Long.parseLong(dataParts[1]);

                deleteLastMessage(msg.getChatId(), msg.getFrom().getId());
                telegramAPIRequests.forwardMessage(channelUsername, String.valueOf(msg.getChatId()), messageId);
                sendAnswer(msg.getChatId(), Configuration.CommandMessage.DELETE_AD, getDeleteAd(messageId));
            }
            case Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT -> {
                deleteLastMessage(msg.getChatId(), msg.getFrom().getId());
                if (Objects.equals(dataParts[1], "1")) {
                    String advertisementText = advertisementService.getAdvertisement(msg.getChatId(), msg.getMessageId())
                            .getAdvertisementText()
                            .substring(Tag.Actual.getName().length() + 1);
                    editAdvertisementText(
                            msg.getChatId(),
                            Integer.parseInt(dataParts[2]),
                            String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT, advertisementText)
                    );
                    advertisementService.removeAdvertisement(Integer.parseInt(dataParts[2]));
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.SUCCESS_DELETE_AD_TEXT);
                } else {
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.UNSUCCESS_DELETE_AD_TEXT);
                    stateService.put(msg.getChatId(), Command.DeleteAdvertisement);
                    getRegisteredCommand(Command.DeleteAdvertisement.getName())
                            .processMessage(this, msg, null);
                }
            }
            default -> log.error("Unknown command in callback data: {}", callbackQuery);
        }
    }

    private static InlineKeyboardButton getInlineKeyboardButton(List<List<InlineKeyboardButton>> buttons, String[] dataParts) {
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
    public void editAdvertisementText(Long chatId, Integer messageId, String text) {
        EditMessageCaption editMessage = new EditMessageCaption();
        String adText = advertisementService.getAdvertisement(chatId, messageId)
                .getAdvertisementText()
                .substring(Tag.Actual.getName().length() + 1);
        String editedText = text.formatted(adText);
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setCaption(editedText);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot edit deleted message: {}", e.getMessage());
        }
    }

    @Override
    public void deleteLastMessage(Long chatId, Long userId) {
        int messageId = lastSentMessageService.getLastSentMessage(chatId, userId).getMessage().getMessageId();
        DeleteMessage deleteLastMessage = new DeleteMessage();
        deleteLastMessage.setMessageId(messageId);
        deleteLastMessage.setChatId(chatId);
        try {
            execute(deleteLastMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot delete last message due to: {}", e.getMessage());
        }
    }

    @Override
    public void editMessageReplyMarkup(Long chatId, List<List<InlineKeyboardButton>> buttons) {
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        ikm.setKeyboard(buttons);
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(lastSentMessageService.get(chatId).getMessageId());
        editMessageReplyMarkup.setReplyMarkup(ikm);
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Cannot edit message reply markup due to: {}", e.getMessage());
        }
    }

    private InlineKeyboardMarkup getDeleteAd(Integer messageId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>(2);

        InlineKeyboardButton yes = new InlineKeyboardButton();
        yes.setText("Да");
        yes.setCallbackData(String.format("%s 1 %d", Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT, messageId));
        buttons.add(yes);

        InlineKeyboardButton no = new InlineKeyboardButton();
        no.setText("Нет");
        no.setCallbackData(String.format("%s 0", Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT));
        buttons.add(no);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(buttons);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    @Override
    public File downloadFileByFilePath(String filePath) {
        try {
            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            log.error("Cannot download file {}", filePath, e);
            return null;
        }
    }

    @Override
    public Message sendPhotoMessage(long chatId, File photoFile, String text) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(Objects.requireNonNull(photoFile)))
                .caption(text)
                .parseMode(ParseMode.HTML)
                .build();

        try {
            return execute(sendPhoto);
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

                    InputMediaPhoto.InputMediaPhotoBuilder inputMediaPhotoBuilder = InputMediaPhoto.builder()
                            .media("attach://" + mediaName)
                            .mediaName(mediaName)
                            .isNewMedia(true)
                            .newMediaFile(Objects.requireNonNull(photoFile))
                            .parseMode(ParseMode.HTML);

                    if (count.getAndIncrement() == 0) {
                        inputMediaPhotoBuilder.caption(text);
                    }

                    return inputMediaPhotoBuilder.build();
                })
                .collect(Collectors.toList());

        SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(medias)
                .build();

        try {
            return execute(sendMediaGroup);
        } catch (TelegramApiException e) {
            log.error("Can't send photos with media group", e);
            return null;
        }
    }

}
