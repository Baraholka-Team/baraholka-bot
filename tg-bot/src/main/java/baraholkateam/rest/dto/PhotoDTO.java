package baraholkateam.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Бизнес сущность фото товаров из объявления пользователя
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDTO implements Serializable {

    @JsonProperty("photoId")
    private Long photoId;
    @JsonProperty("photo")
    private String photo;

}
