package baraholkateam;

import baraholkateam.bot.BaraholkaBot;
import baraholkateam.command.BaraholkaBotCommand;
import baraholkateam.configuration.BaraholkaBotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Slf4j
@EnableJpaRepositories("baraholkateam.rest.repository")
@EntityScan("baraholkateam.rest.model")
@EnableScheduling
@SpringBootApplication
public class BaraholkaBotMain {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BaraholkaBotMain.class, args);
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        // Получение бина класса бота
        BaraholkaBot bot = context.getBean(BaraholkaBot.class);

        // Регистрация команд для бота
        Map<String, BaraholkaBotCommand> commandMap = context.getBeansOfType(BaraholkaBotCommand.class);
        bot.registerAll(commandMap.values().toArray(BaraholkaBotCommand[]::new));

        // Получение конфигурации бота
        BaraholkaBotConfiguration botConfiguration = context.getBean(BaraholkaBotConfiguration.class);
        String botToken = botConfiguration.getToken();

        // Регистрация бота
        try {
            botsApplication.registerBot(botToken, bot);
        } catch (TelegramApiException e) {
            log.error("Невозможно запустить бота", e);
            throw new RuntimeException(e);
        }
    }

}
