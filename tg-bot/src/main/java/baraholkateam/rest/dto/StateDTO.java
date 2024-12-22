package baraholkateam.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Бизнес сущность состояния бота
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateDTO implements Serializable {

    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("current_command")
    private CommandDTO currentCommand;
    @JsonProperty("previous_command")
    private CommandDTO previousCommand;

}
