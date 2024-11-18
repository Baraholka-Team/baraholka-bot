package baraholkateam.bot;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.List;

public interface MediaSender {

    Message sendPhotoMessage(long chatId, File photoFile, String text);

    List<Message> sendPhotoMediaGroup(long chatId, List<File> photoFiles, String text);

}
