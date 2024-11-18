package baraholkateam.command;

import baraholkateam.rest.service.LastSentMessageService;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import baraholkateam.util.TagType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public abstract class BaraholkaBotCommand extends BotCommand {

    @Autowired
    private LastSentMessageService lastSentMessageService;
    ReplyKeyboard replyKeyboard;

    public BaraholkaBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    void sendAnswer(AbsSender absSender, User user, Chat chat, String text) {
        sendAnswer(absSender, user, chat, text, false);
    }

    void sendAnswer(AbsSender absSender, User user, Chat chat, String text, boolean withReplyKeyboard) {
        Long chatId = chat.getId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode(ParseMode.HTML);
        message.setText(text);
        message.disableWebPagePreview();
        if (withReplyKeyboard && replyKeyboard != null) {
            message.setReplyMarkup(replyKeyboard);
        } else {
            KeyboardButton back = new KeyboardButton();
            back.setText(Configuration.CommandMessage.BACK_BUTTON);
            KeyboardRow line = new KeyboardRow();
            line.add(back);
            List<KeyboardRow> lines = new ArrayList<>(1);
            lines.add(line);
            ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
            rkm.setKeyboard(lines);
            rkm.setResizeKeyboard(true);
            message.setReplyMarkup(rkm);
        }

        try {
            Message sentMessage = absSender.execute(message);
            lastSentMessageService.put(chatId, sentMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot execute command /{} of user {}: {}", getCommandIdentifier(), user.getUserName(), e.getMessage());
        }
    }

    void prepareNextButton() {
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setSelective(true);
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(true);
        List<KeyboardRow> nextList = new ArrayList<>(1);
        KeyboardRow next = new KeyboardRow();
        next.add(new KeyboardButton(Configuration.CommandMessage.NEXT_BUTTON_TEXT));
        nextList.add(next);

        KeyboardButton back = new KeyboardButton();
        back.setText(Configuration.CommandMessage.BACK_BUTTON);
        KeyboardRow line = new KeyboardRow();
        line.add(back);
        nextList.add(line);

        KeyboardButton menu = new KeyboardButton();
        menu.setText(Command.MainMenu.getDescription());
        KeyboardRow menuLine = new KeyboardRow();
        menuLine.add(menu);
        nextList.add(menuLine);

        rkm.setKeyboard(nextList);

        replyKeyboard = rkm;
    }

    void prepareTags(TagType tagType, Boolean isMultipleChoice) {
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> tags = new ArrayList<>(1);
        int count = 0;
        int i = 0;
        List<InlineKeyboardButton> tagButton = new ArrayList<>(2);
        for (Tag tag : Tag.values()) {
            if (tag.getTagType() == tagType) {
                String text = tag.getName();
                String callbackData = String.format("%s %s", Configuration.CommandMessage.TAG_CALLBACK_DATA, tag.getName());
                if (isMultipleChoice) {
                    text = String.format(Configuration.CommandMessage.NOT_CHOSEN_TAG, text);
                    callbackData = String.format("%s %s %d %d 0", Configuration.CommandMessage.TAGS_CALLBACK_DATA, tag.getName(),
                            Math.floorDiv(count, 2), i % 2 == 0 ? 0 : 1);
                    count++;
                }

                tagButton.add(InlineKeyboardButton.builder()
                        .text(text)
                        .callbackData(callbackData)
                        .build());

                if (i++ % 2 == 1) {
                    tags.add(tagButton);
                    tagButton = new ArrayList<>(2);
                }
            }
        }
        if (i % 2 == 1) {
            tags.add(tagButton);
        }
        ikm.setKeyboard(tags);

        replyKeyboard = ikm;
    }

    ReplyKeyboardMarkup prepareReplyKeyboard(List<String> buttons, boolean isAddMenuButton) {
        List<KeyboardRow> lines = prepareLines(buttons);

        if (isAddMenuButton) {
            KeyboardButton menu = new KeyboardButton();
            menu.setText(Command.MainMenu.getDescription());
            KeyboardRow menuLine = new KeyboardRow();
            menuLine.add(menu);
            lines.add(menuLine);
        }

        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setKeyboard(lines);
        rkm.setResizeKeyboard(true);
        return rkm;
    }

    private static List<KeyboardRow> prepareLines(List<String> buttons) {
        List<KeyboardRow> lines = new ArrayList<>(1);
        KeyboardRow line = new KeyboardRow();

        if (buttons.size() == 1) {
            line.add(buttons.get(0));
            lines.add(line);
        } else {
            for (int i = 0; i < buttons.size(); i++) {
                if (i % 2 == 0) {
                    line = new KeyboardRow();
                    line.add(buttons.get(i));
                } else {
                    line.add(buttons.get(i));
                    lines.add(line);
                }
            }
        }

        if (buttons.size() > 1 && buttons.size() % 2 == 1) {
            lines.add(line);
        }

        KeyboardButton back = new KeyboardButton();
        back.setText(Configuration.CommandMessage.BACK_BUTTON);
        line = new KeyboardRow();
        line.add(back);
        lines.add(line);

        return lines;
    }

}
