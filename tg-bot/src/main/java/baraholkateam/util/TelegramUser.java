package baraholkateam.util;

/**
 * Данные пользователя Телеграмма.
 * @param userId id пользователя
 * @param firstName имя
 * @param lastName фамилия
 * @param username  никнейм
 */
public record TelegramUser(Integer userId, String firstName, String lastName, String username) {

}
