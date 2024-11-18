package baraholkateam.bot;

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
import baraholkateam.command.SearchAdvertisementsAddAdvertisementTypes;
import baraholkateam.command.SearchAdvertisementsAddProductCategories;
import baraholkateam.command.SearchAdvertisementsShowFoundAdvertisementsCommand;
import baraholkateam.command.StartCommand;
import baraholkateam.command.UserAdvertisementsCommand;
import baraholkateam.notification.NotificationExecutor;
import baraholkateam.rest.model.CurrentAdvertisement;
import baraholkateam.rest.service.ActualAdvertisementService;
import baraholkateam.rest.service.ChosenTagsService;
import baraholkateam.rest.service.CurrentAdvertisementService;
import baraholkateam.rest.service.CurrentStateService;
import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.rest.service.PreviousStateService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.Converter;
import baraholkateam.util.Command;
import baraholkateam.util.Tag;
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
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static baraholkateam.notification.NotificationExecutor.FIRST_REPEAT_NOTIFICATION_PERIOD;
import static baraholkateam.notification.NotificationExecutor.FIRST_REPEAT_NOTIFICATION_TIME_UNIT;

@Slf4j
@Component
public class BaraholkaBot extends TelegramLongPollingCommandBot implements FileLoader, MediaSender {

    /**
     * Лимит выдачи объявлений в функции поиска объявлений по тегам.
     */
    public static final Integer SEARCH_ADVERTISEMENTS_LIMIT = 10;
    private final String botName;
    private final String botToken;
    @Value("${channel.chat_id}")
    private String channelChatId;
    @Value("${channel.username}")
    private String channelUsername;
    @Autowired
    private ActualAdvertisementService actualAdvertisementService;
    @Autowired
    private CurrentAdvertisementService currentAdvertisementService;
    @Autowired
    private CurrentStateService currentStateService;
    @Autowired
    private LastSentMessageService lastSentMessageService;
    @Autowired
    private ChosenTagsService chosenTagsService;
    @Autowired
    private PreviousStateService previousStateService;
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
    private SearchAdvertisementsAddAdvertisementTypes searchAdvertisementsAddAdvertisementTypes;
    @Autowired
    private SearchAdvertisementsAddProductCategories searchAdvertisementsAddProductCategories;
    @Autowired
    private SearchAdvertisementsShowFoundAdvertisementsCommand searchAdvertisementsShowFoundAdvertisements;

    public BaraholkaBot(
            @Value("${bot.name}") String botName,
            @Value("${bot.token}") String botToken
    ) {
        super(botToken);
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Устанавливает бота в определенное состояние в зависимости от введенной пользователем команды.
     * @param message отправленное пользователем сообщение
     * @return false, так как боту необходимо всегда обработать входящее сообщение
     */
    @Override
    public boolean filter(Message message) {
        //  Случай ввода команды по идентификатору
        Command currentCommandByIdentifier = Command.findCommand(message.getText().replace("/", ""));
        if (currentCommandByIdentifier != null) {
            currentStateService.put(message.getChatId(), currentCommandByIdentifier);
            return false;
        }

        // Случай нажатия на кнопку с описанием команды
        Command currentCommandByDescription = Command.findCommandByDescription(message.getText());
        if (currentCommandByDescription != null) {
            currentStateService.put(message.getChatId(), currentCommandByDescription);
            return false;
        }

        // Случай пропуска этапа добавления цены, если не были добавлены соответствующие теги
        if (currentStateService.get(message.getChatId()) == Command.NewAdvertisement_AddPrice) {
            List<String> tags = currentAdvertisementService.getTags(message.getChatId());
            if (!tags.contains(Tag.Sale.getName()) && !tags.contains(Tag.Bargaining.getName())) {
                currentStateService.put(message.getChatId(), Command.NewAdvertisement_AddContacts);
            }
        }
        return false;
    }

    @Override
    public void onRegister() {
        super.onRegister();

        register(startCommand);
        register(helpCommand);
        register(mainMenuCommand);
        register(userAdvertisementsCommand);
        register(deleteAdvertisementCommand);
        register(newAdvertisementCommand);
        register(newAdvertisementAddPhotosCommand);
        register(newAdvertisementConfirmPhotoCommand);
        register(newAdvertisementAddDescriptionCommand);
        register(newAdvertisementAddCityCommand);
        register(newAdvertisementAddAdvertisementTypesCommand);
        register(newAdvertisementAddCategoriesCommand);
        register(newAdvertisementAddPriceCommand);
        register(newAdvertisementConfirmPriceCommand);
        register(newAdvertisementAddContactsCommand);
        register(newAdvertisementAddPhoneCommand);
        register(newAdvertisementConfirmPhoneCommand);
        register(newAdvertisementAddSocialCommand);
        register(newAdvertisementConfirmCommand);
        register(searchAdvertisementsCommand);
        register(searchAdvertisementsAddAdvertisementTypes);
        register(searchAdvertisementsAddProductCategories);
        register(searchAdvertisementsShowFoundAdvertisements);
    }

    @Override
    public void processInvalidCommandUpdate(Update update) {
        Message msg = update.getMessage();

        if (msg == null) {
            return;
        }

        Long chatId = msg.getChatId();

        sendAnswer(chatId, Configuration.CommandMessage.UNKNOWN_COMMAND);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg;

        // Случай получения информации с кнопок (инлайн-клавиатуры)
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            msg = (Message) callbackQuery.getMessage();
            String callbackQueryData = callbackQuery.getData();
            parseKeyboardData(callbackQueryData, msg);
            return;
        }

        msg = update.getMessage();

        if (msg == null) {
            return;
        }

        Long chatId = msg.getChatId();

        // Случай нажатия на кнопку "Назад"
        if (msg.hasText() && Objects.equals(msg.getText(), Configuration.CommandMessage.BACK_BUTTON)) {
            Command currentCommand = currentStateService.get(chatId);
            Command backCommand = Command.previousCommand(currentCommand);
            if (backCommand != null) {
                if (lastSentMessageService.get(chatId).hasReplyMarkup()) {
                    deleteLastMessage(chatId);
                }
                if (currentCommand == Command.NewAdvertisement_AddSocial) {
                    currentAdvertisementService.setSocials(chatId, new ArrayList<>());
                }
                currentStateService.put(chatId, backCommand);
                if (backCommand == Command.NewAdvertisement_AddContacts) {
                    currentAdvertisementService.setSocials(chatId, new ArrayList<>());
                }
                getRegisteredCommand(backCommand.getIdentifier()).processMessage(this, msg, null);
                return;
            }
        }

        // Случай обновления данных во время создания объявления
        if (msg.hasText() && newAdvertisementUpdateData(msg)) {
            return;
        }

        // Случай добавления новых фотографий во время создания объявления
        if (msg.hasPhoto() && addAdvertisementPhotos(msg)) {
            return;
        }

        // Случай нажатия на кнопку с описанием команды
        Command commandByDescription = Command.findCommandByDescription(msg.getText());
        if (commandByDescription != null) {
            currentStateService.put(chatId, commandByDescription);
            getRegisteredCommand(commandByDescription.getIdentifier()).processMessage(this, msg, null);
            return;
        }

        // Случай удаления всех фотографий по кнопке
        if (msg.hasText() && Objects.equals(msg.getText(), Configuration.CommandMessage.DELETE_ALL_PHOTOS)
                && currentStateService.get(chatId) == Command.NewAdvertisement_ConfirmPhoto) {
            currentAdvertisementService.setPhotos(msg.getChatId(), new ArrayList<>());
            sendAnswer(chatId, Configuration.CommandMessage.PHOTOS_DELETE);
            currentStateService.put(chatId, Command.NewAdvertisement_AddPhotos);
            getRegisteredCommand(Command.NewAdvertisement_AddPhotos.getIdentifier()).processMessage(this, msg, null);
            return;
        }

        // Случай удаления всех социальных сетей по кнопке
        if (msg.hasText() && Objects.equals(msg.getText(), Configuration.CommandMessage.DELETE_ALL_SOCIALS)
                && currentStateService.get(chatId) == Command.NewAdvertisement_ConfirmPhone) {
            currentAdvertisementService.setSocials(msg.getChatId(), new ArrayList<>());
            sendAnswer(chatId, Configuration.CommandMessage.SOCIALS_DELETE);
            currentStateService.put(chatId, Command.NewAdvertisement_ConfirmPhone);
            getRegisteredCommand(Command.NewAdvertisement_ConfirmPhone.getIdentifier()).processMessage(this, msg, null);
            return;
        }

        // Случай нажатия на кнопку "Продолжить" во множественном выборе хэштегов
        if (msg.hasText() && Objects.equals(msg.getText(), Configuration.CommandMessage.NEXT_BUTTON_TEXT)) {
            addChosenTags(lastSentMessageService.get(chatId));

            if (chosenTagsService.get(chatId) != null
                    && Command.nextCommand(currentStateService.get(msg.getChatId())) == Command.NewAdvertisement_AddPrice) {
                List<String> addedTags = currentAdvertisementService.getTags(chatId);
                // Если нужно пропустить добавление цены товара
                if (!addedTags.contains(Tag.Sale.getName()) && !addedTags.contains(Tag.Bargaining.getName())) {
                    List<String> tags = chosenTagsService.get(chatId).stream()
                            .map(Tag::getName)
                            .toList();
                    previousStateService.put(chatId, currentStateService.get(msg.getChatId()));
                    currentAdvertisementService.addTags(chatId, tags);
                    currentStateService.put(msg.getChatId(), Command.NewAdvertisement_AddContacts);
                    deleteLastMessage(msg.getChatId());
                    sendAnswer(
                            chatId,
                            String.format(
                                    Configuration.CommandMessage.CHOSEN_HASHTAGS,
                                    String.join(" ", currentAdvertisementService.getTags(chatId))
                            )
                    );
                    getRegisteredCommand(Command.NewAdvertisement_AddContacts.getIdentifier())
                            .processMessage(this, msg, null);
                    return;
                }
            }

            if (chosenTagsService.get(chatId) != null
                    && (currentStateService.get(msg.getChatId()) == Command.NewAdvertisement_AddAdvertisementTypes
                    || currentStateService.get(msg.getChatId()) == Command.NewAdvertisement_AddCategories)) {
                List<String> tags = chosenTagsService.get(chatId).stream()
                        .map(Tag::getName)
                        .toList();
                currentAdvertisementService.addTags(chatId, tags);
                chosenTagsService.delete(chatId);
            }

            deleteLastMessage(msg.getChatId());
            Command nextCommand = Command.nextCommand(currentStateService.get(msg.getChatId()));
            previousStateService.put(chatId, currentStateService.get(msg.getChatId()));
            currentStateService.put(chatId, nextCommand);
            getRegisteredCommand(nextCommand.getIdentifier()).processMessage(this, msg, null);
            return;
        }

        // Случай обработки текстовой информации от пользователя
        Command currentCommand = currentStateService.get(chatId);
        if (currentCommand != null) {
            executeNonCommand(msg, chatId, currentCommand);
        }
    }

    private boolean newAdvertisementUpdateData(Message msg) {
        Command command = currentStateService.get(msg.getChatId());
        String text = msg.getText();
        if (command == Command.NewAdvertisement_AddDescription) {
            if (text == null || text.length() > 800) {
                return false;
            }
            Pattern filter = Pattern.compile(Configuration.CommandMessage.SWEAR_WORD_DETECTOR, Pattern.CASE_INSENSITIVE);
            Matcher matcher = filter.matcher(text);
            if (matcher.find()) {
                sendAnswer(msg.getChatId(), Configuration.CommandMessage.AD_SWEAR_WORD_DETECTED);
                return true;
            }
            currentAdvertisementService.setDescription(msg.getChatId(), text);
            updateStateOnTextData(msg);
            return true;
        }

        if (command == Command.NewAdvertisement_AddPrice) {
            if (!text.matches("\\d{1,18}")) {
                return false;
            }
            try {
                currentAdvertisementService.setPrice(msg.getChatId(), Long.parseLong(text));
            } catch (Exception e) {
                log.error("Invalid input from user");
            }
            updateStateOnTextData(msg);
            return true;
        }

        if (command == Command.NewAdvertisement_AddPhone) {
            if (!text.matches("\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}")) {
                return false;
            }
            currentAdvertisementService.setPhone(msg.getChatId(), text);
            updateStateOnTextData(msg);
            return true;
        }

        if (command == Command.NewAdvertisement_AddSocial) {
            if (!text.matches("https://.+/.+")) {
                return false;
            }
            currentAdvertisementService.addSocial(msg.getChatId(), text);
            previousStateService.put(msg.getChatId(), Command.NewAdvertisement_AddSocial);
            currentStateService.put(msg.getChatId(), Command.NewAdvertisement_ConfirmPhone);
            getRegisteredCommand(Command.NewAdvertisement_ConfirmPhone.getIdentifier())
                    .processMessage(this, msg, null);
            return true;
        }
        return false;
    }

    private boolean addAdvertisementPhotos(Message msg) {
        Command currentCommand = currentStateService.get(msg.getChatId());
        if (currentCommand == Command.NewAdvertisement_AddPhotos || currentCommand == Command.NewAdvertisement_ConfirmPhoto) {

            AtomicInteger canAddPhotosCount =
                    new AtomicInteger(10 - currentAdvertisementService.getPhotos(msg.getChatId()).size());
            AtomicBoolean isCanAdd = new AtomicBoolean(true);

            Map<String, TreeSet<PhotoSize>> photos = msg.getPhoto().stream()
                    .collect(
                            Collectors.groupingBy(
                                    // FIXME мапим пока только по первой половине символов id фотографий
                                    photo -> photo.getFileId().substring(0, photo.getFileId().length() / 2),
                                    Collectors.toCollection(
                                            () -> new TreeSet<>(Comparator.comparingInt(PhotoSize::getWidth))
                                    )
                            )
                    );

            // добавляем фотографии самого высокого разрешения из числа одинаковых фотографий разного разрешения
            photos.forEach((make, photo) -> {
                if (canAddPhotosCount.getAndDecrement() <= 0) {
                    deleteLastMessage(msg.getChatId());
                    isCanAdd.set(false);
                    if (lastSentMessageService.get(msg.getChatId()).getText().substring(0, 20)
                            .equals(Configuration.CommandMessage.NO_MORE_PHOTOS_ADD.substring(0, 20))) {
                        deleteLastMessage(msg.getChatId());
                    }
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.NO_MORE_PHOTOS_ADD);
                    return;
                }
                currentAdvertisementService.addPhoto(
                        msg.getChatId(),
                        Converter.convertPhotoToBase64String(
                                downloadFileByFilePath(telegramAPIRequests.getFilePath(photo.last().getFileId()))
                        )
                );
            });

            if (isCanAdd.get()) {
                getRegisteredCommand(Command.NewAdvertisement_ConfirmPhoto.getIdentifier())
                        .processMessage(this, msg, null);
                currentStateService.put(msg.getChatId(), Command.NewAdvertisement_ConfirmPhoto);
            }
            return true;
        }
        return false;
    }

    private void updateStateOnTextData(Message msg) {
        Command nextCommand = Command.nextCommand(currentStateService.get(msg.getChatId()));
        previousStateService.put(msg.getChatId(), currentStateService.get(msg.getChatId()));
        currentStateService.put(msg.getChatId(), nextCommand);
        getRegisteredCommand(nextCommand.getIdentifier()).processMessage(this, msg, null);
    }

    private void executeNonCommand(Message msg, Long chatId, Command currCommand) {
        List<NonCommand.AnswerPair> answers = nonCommand.nonCommandExecute(msg, currCommand);
        if (answers.get(0).getError()) {
            for (NonCommand.AnswerPair answer : answers) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(answer.getAnswer());
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
                getRegisteredCommand(currCommand.getIdentifier()).processMessage(this, msg, null);
            }
            return;
        } else {
            Command nextCommand = Command.nextCommand(currentStateService.get(msg.getChatId()));
            previousStateService.put(chatId, currentStateService.get(msg.getChatId()));
            currentStateService.put(chatId, nextCommand);
        }
        // ошибки в обработке сообщения пользователя нет, отправляем ответ и переходим на следующий шаг
        for (NonCommand.AnswerPair answer : answers) {
            if (!answer.getError()) {
                sendAnswer(
                        chatId,
                        answer.getAnswer(),
                        answer.getReplyKeyboard() == null ? null : answer.getReplyKeyboard()
                );
            }
        }
    }

    private void sendAnswer(Long chatId, String text) {
        sendAnswer(chatId, text, null);
    }

    private void sendAnswer(Long chatId, String text, ReplyKeyboard replyKeyboard) {
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
                                        .map(Tag::getTagByName)
                                        .toList());
                    } else {
                        String chosenTagsString = tags.stream()
                                .map(Tag::getName)
                                .collect(Collectors.joining(" "));
                        chosenTagsService.put(message.getChatId(),
                                Arrays.stream(String.format("%s %s", chosenTagsString, newTags)
                                        .split(" "))
                                        .map(Tag::getTagByName)
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
                if (currentStateService.get(msg.getChatId()) == Command.NewAdvertisement_AddCity) {
                    currentAdvertisementService.addTag(msg.getChatId(), dataParts[1]);
                } else {
                    List<Tag> tags = chosenTagsService.get(msg.getChatId());
                    if (tags == null || tags.isEmpty()) {
                        chosenTagsService.put(msg.getChatId(),
                                Arrays.stream(dataParts[1].split(" "))
                                        .map(Tag::getTagByName)
                                        .toList());
                    } else {
                        String chosenTagsString = tags.stream()
                                .map(Tag::getName)
                                .collect(Collectors.joining(" "));
                        chosenTagsService.put(msg.getChatId(),
                                Arrays.stream(String.format("%s %s", chosenTagsString, dataParts[1]).split(" "))
                                        .map(Tag::getTagByName)
                                        .toList());
                    }
                }

                deleteLastMessage(msg.getChatId());
                Command nextCommand = Command.nextCommand(currentStateService.get(msg.getChatId()));
                previousStateService.put(msg.getChatId(), currentStateService.get(msg.getChatId()));
                currentStateService.put(msg.getChatId(), nextCommand);
                getRegisteredCommand(nextCommand.getIdentifier()).processMessage(this, msg, null);
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
                deleteLastMessage(msg.getChatId());
                if (Objects.equals(dataParts[1], "yes")) {
                    currentStateService.put(msg.getChatId(), Command.NewAdvertisement_AddPhone);
                    getRegisteredCommand(Command.NewAdvertisement_AddPhone.getIdentifier())
                            .processMessage(this, msg, null);
                } else if (Objects.equals(dataParts[1], "no")) {
                    currentStateService.put(msg.getChatId(), Command.NewAdvertisement_ConfirmPhone);
                    getRegisteredCommand(Command.NewAdvertisement_ConfirmPhone.getIdentifier())
                            .processMessage(this, msg, null);
                }
            }
            case Configuration.CommandMessage.SOCIAL_CALLBACK_DATA -> {
                deleteLastMessage(msg.getChatId());
                if (Objects.equals(dataParts[1], "yes")) {
                    currentStateService.put(msg.getChatId(), Command.NewAdvertisement_AddSocial);
                    getRegisteredCommand(Command.NewAdvertisement_AddSocial.getIdentifier())
                            .processMessage(this, msg, null);
                } else if (Objects.equals(dataParts[1], "no")) {
                    currentStateService.put(msg.getChatId(), Command.NewAdvertisement_Confirm);
                    getRegisteredCommand(Command.NewAdvertisement_Confirm.getIdentifier())
                            .processMessage(this, msg, null);
                }
            }
            case Configuration.CommandMessage.CONFIRM_AD_CALLBACK_DATA -> {
                deleteLastMessage(msg.getChatId());
                if (Objects.equals(dataParts[1], "yes")) {
                    if (currentAdvertisementService.getContacts(msg.getChatId()).isEmpty()
                            && currentAdvertisementService.getPhone(msg.getChatId()) == null) {
                        currentAdvertisementService.setSocials(msg.getChatId(),
                                List.of("@" + telegramAPIRequests.getUser(msg.getChatId()).username()));
                    }

                    Message sentAd;
                    if (Objects.equals(dataParts[2], "0")) {
                        sentAd = sendPhotoMessage(
                                Long.parseLong(channelChatId),
                                Converter.convertBase64StringToPhoto(
                                        currentAdvertisementService.getPhotos(msg.getChatId()).get(0)
                                ),
                                currentAdvertisementService.getAdvertisementText(msg.getChatId())
                        );
                    } else {
                        List<File> photoFiles = new ArrayList<>();
                        for (String photo : currentAdvertisementService.getPhotos(msg.getChatId())) {
                            photoFiles.add(Objects.requireNonNull(Converter.convertBase64StringToPhoto(photo)));
                        }
                        sentAd = sendPhotoMediaGroup(
                                Long.parseLong(channelChatId),
                                photoFiles,
                                currentAdvertisementService.getAdvertisementText(msg.getChatId())
                        ).get(0);
                    }
                    if (sentAd != null) {
                        CurrentAdvertisement currentAdvertisement = currentAdvertisementService.get(msg.getChatId());
                        currentAdvertisement
                                .setMessageId(Long.parseLong(String.valueOf(sentAd.getMessageId())))
                                .setCreationTime(System.currentTimeMillis())
                                .setNextUpdateTime(
                                        System.currentTimeMillis()
                                                + FIRST_REPEAT_NOTIFICATION_TIME_UNIT
                                                .toMillis(FIRST_REPEAT_NOTIFICATION_PERIOD)
                                )
                                .setUpdateAttempt(0);
                        currentAdvertisementService.put(currentAdvertisement);
                        actualAdvertisementService.insertNewAdvertisement(currentAdvertisement);
                        sendAnswer(msg.getChatId(), Configuration.CommandMessage.SUCCESS_TEXT);
                    } else {
                        sendAnswer(msg.getChatId(), Configuration.CommandMessage.UNSUCCESS_TEXT);
                        log.error("Error while sending advertisement to channel.");
                    }
                } else if (Objects.equals(dataParts[1], "no")) {
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.ADVERTISEMENT_CANCELLED_TEXT);
                }
                currentStateService.put(msg.getChatId(), Command.MainMenu);
                getRegisteredCommand(Command.MainMenu.getIdentifier())
                        .processMessage(this, msg, null);
            }
            case Configuration.CommandMessage.NOTIFICATION_CALLBACK_DATA -> {
                long chatId = Long.parseLong(dataParts[1]);
                long messageId = Long.parseLong(dataParts[2]);

                if (Objects.equals(dataParts[4], "0")) {
                    editAdText(dataParts[2]);
                    actualAdvertisementService.removeAdvertisement(messageId);
                    sendAnswer(chatId, Configuration.CommandMessage.ADVERTISEMENT_SUCCESSFUL_DELETE);
                } else {
                    actualAdvertisementService.setNextUpdateTime(messageId,
                            System.currentTimeMillis() + Long.parseLong(dataParts[3]));
                    actualAdvertisementService.setUpdateAttempt(messageId, 0);
                    sendAnswer(chatId, Configuration.CommandMessage.ADVERTISEMENT_SUCCESSFUL_UPDATE);
                }
                notificationExecutor.deleteMessages(this, chatId, messageId);
            }
            case Configuration.CommandMessage.DELETE_CALLBACK_TEXT -> {
                long messageId = Long.parseLong(dataParts[1]);

                deleteLastMessage(msg.getChatId());
                telegramAPIRequests.forwardMessage(channelUsername, String.valueOf(msg.getChatId()), messageId);
                sendAnswer(msg.getChatId(), Configuration.CommandMessage.DELETE_AD, getDeleteAd(messageId));
            }
            case Configuration.CommandMessage.DELETE_AD_CALLBACK_TEXT -> {
                deleteLastMessage(msg.getChatId());
                if (Objects.equals(dataParts[1], "1")) {
                    editAdText(dataParts[2]);
                    actualAdvertisementService.removeAdvertisement(Long.parseLong(dataParts[2]));
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.SUCCESS_DELETE_AD_TEXT);
                } else {
                    sendAnswer(msg.getChatId(), Configuration.CommandMessage.UNSUCCESS_DELETE_AD_TEXT);
                    currentStateService.put(msg.getChatId(), Command.DeleteAdvertisement);
                    getRegisteredCommand(Command.DeleteAdvertisement.getIdentifier())
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

    public void editAdText(String messageId) {
        EditMessageCaption editMessage = new EditMessageCaption();
        String adText = actualAdvertisementService.adText(Long.parseLong(messageId))
                .substring(Tag.Actual.getName().length() + 1);
        String editedText = String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT, adText);
        editMessage.setChatId(channelChatId);
        editMessage.setMessageId(Integer.parseInt(messageId));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setCaption(editedText);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot edit deleted message: {}", e.getMessage());
        }
    }

    private void deleteLastMessage(Long chatId) {
        DeleteMessage deleteLastMessage = new DeleteMessage();
        deleteLastMessage.setMessageId(lastSentMessageService.get(chatId).getMessageId());
        deleteLastMessage.setChatId(chatId);
        try {
            execute(deleteLastMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot delete last message due to: {}", e.getMessage());
        }
    }

    private void editMessageReplyMarkup(Long chatId, List<List<InlineKeyboardButton>> buttons) {
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

    private InlineKeyboardMarkup getDeleteAd(Long messageId) {
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
                }).collect(Collectors.toList());

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
