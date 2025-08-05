package baraholkateam.rest.mapper;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.model.CommandEntity;
import baraholkateam.util.Command;

/**
 * Маппер бизнес сущности команды пользователя и JPA сущности
 */
public class CommandMapper {

    public static CommandEntity getCommandEntity(CommandDTO commandDTO) {
        return CommandEntity.builder()
                .commandId(commandDTO.getCommandId())
                .name(commandDTO.getCommand().getName())
                .description(commandDTO.getCommand().getDescription())
                .build();
    }

    public static CommandDTO getCommandDTO(CommandEntity commandEntity) throws BaraholkaBotException {
        return CommandDTO.builder()
                .commandId(commandEntity.getCommandId())
                .command(Command.findCommand(commandEntity.getName()))
                .build();
    }

}
