package baraholkateam.rest.mapper;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.CommandOrderDTO;
import baraholkateam.rest.model.CommandOrderEntity;

/**
 * Маппер бизнес сущности порядка команд и JPA сущности
 */
public class CommandOrderMapper {

    public static CommandOrderEntity getCommandOrderEntity(CommandOrderDTO commandOrderDTO) {
        return CommandOrderEntity.builder()
                .commandOrderId(commandOrderDTO.getCommandOrderId())
                .currentCommand(CommandMapper.getCommandEntity(commandOrderDTO.getCurrentCommand()))
                .nextCommand(CommandMapper.getCommandEntity(commandOrderDTO.getNextCommand()))
                .build();
    }

    public static CommandOrderDTO getCommandOrderDTO(CommandOrderEntity commandOrderEntity) throws BaraholkaBotException {
        return CommandOrderDTO.builder()
                .commandOrderId(commandOrderEntity.getCommandOrderId())
                .currentCommand(CommandMapper.getCommandDTO(commandOrderEntity.getCurrentCommand()))
                .nextCommand(CommandMapper.getCommandDTO(commandOrderEntity.getNextCommand()))
                .build();
    }

}
