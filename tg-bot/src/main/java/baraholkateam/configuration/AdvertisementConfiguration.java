package baraholkateam.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "advertisement")
@Getter
@Setter
public class AdvertisementConfiguration {

    private Integer searchAdvertisementLimit;
    private Integer descriptionLength;
    private String phone;
    private String social;

}
