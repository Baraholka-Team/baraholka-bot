package baraholkateam.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
public class PhotoConverter {

    public static String convertPhotoToBase64String(File photo) {
        try {
            return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(photo));
        } catch (IOException e) {
            log.error("Cannot convert photo to base64 string", e);
            return null;
        }
    }

    public static File convertBase64StringToPhoto(String photo) {
        try {
            File photoFile = File.createTempFile("photo", "temp");
            Files.write(Path.of(photoFile.getPath()), Base64.getDecoder().decode(photo));
            return photoFile;
        } catch (IOException e) {
            log.error("Cannot convert base64 string to photo", e);
            return null;
        }
    }
}
