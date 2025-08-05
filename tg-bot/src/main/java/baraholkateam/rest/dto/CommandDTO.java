package baraholkateam.rest.dto;

import baraholkateam.util.Command;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Бизнес сущность команды пользователя
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandDTO implements Serializable {

    @JsonProperty("command_id")
    private Long commandId;
    @JsonProperty("command")
    private Command command;

}
