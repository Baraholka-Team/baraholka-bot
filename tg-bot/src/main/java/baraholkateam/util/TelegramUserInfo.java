package baraholkateam.util;

import java.io.Serializable;

public record TelegramUserInfo(Long id, String first_name, String last_name, String username, String photo_url,
                               Integer auth_date, String hash) implements Serializable {

    public String getCheckString() {
        return "auth_date=" + auth_date + "\n"
                + "first_name=" + first_name + "\n"
                + "id=" + id + "\n"
                + "last_name=" + last_name + "\n"
                + "photo_url=" + photo_url + "\n"
                + "username=" + username;
    }

}
