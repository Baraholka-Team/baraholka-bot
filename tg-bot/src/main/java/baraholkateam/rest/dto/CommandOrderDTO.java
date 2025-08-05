package baraholkateam.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Бизнес сущность порядка команд в боте
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandOrderDTO {

    @JsonProperty("command_order_id")
    private Long commandOrderId;
    @JsonProperty("current_command")
    private CommandDTO currentCommand;
    @JsonProperty("next_command")
    private CommandDTO nextCommand;

}
