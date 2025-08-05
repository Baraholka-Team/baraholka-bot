package baraholkateam.configuration;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@ConfigurationProperties(prefix = "bot")
@Getter
@Setter
public class BaraholkaBotConfiguration {

    private String token;
    private String name;
    private boolean allowCommandsWithUsername;

    @Bean
    public OkHttpClient httpClient() { //TODO настроить http клиента
        return new OkHttpClient.Builder()
                .build();
    }

    @Bean
    public TelegramClient telegramClient(OkHttpClient okClient) {
        return new OkHttpTelegramClient(okClient, token);
    }

}
