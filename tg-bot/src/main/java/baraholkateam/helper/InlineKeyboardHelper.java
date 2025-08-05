package baraholkateam.helper;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InlineKeyboardHelper {

    public static List<String> getChosenTags(Message message) {
        List<String> chosenTags = new ArrayList<>();
        List<InlineKeyboardRow> buttonList = message.getReplyMarkup().getKeyboard();
        for (InlineKeyboardRow row : buttonList) {
            row.iterator().forEachRemaining(button -> {
                String[] dataCallbackParts = button.getCallbackData().split(" ");
                if (dataCallbackParts.length == 2 && Objects.equals(dataCallbackParts[0], "1")) {
                    chosenTags.add(dataCallbackParts[1]);
                }
            });
        }
        return chosenTags;
    }

}
