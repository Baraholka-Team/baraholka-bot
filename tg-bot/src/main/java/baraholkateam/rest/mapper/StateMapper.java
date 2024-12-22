package baraholkateam.rest.mapper;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.StateDTO;
import baraholkateam.rest.model.StateEntity;
import baraholkateam.rest.model.StateEntityId;

/**
 * Маппер бизнес сущности состояния бота и JPA сущности
 */
public class StateMapper {

    public static StateEntity getStateEntity(StateDTO stateDTO) {
        StateEntityId stateEntityId = StateEntityId.builder()
                .chatId(stateDTO.getChatId())
                .userId(stateDTO.getUserId())
                .build();

        return StateEntity.builder()
                .stateEntityId(stateEntityId)
                .currentCommand(CommandMapper.getCommandEntity(stateDTO.getCurrentCommand()))
                .previousCommand(CommandMapper.getCommandEntity(stateDTO.getPreviousCommand()))
                .build();
    }

    public static StateDTO getStateDTO(StateEntity stateEntity) throws BaraholkaBotException {
        return StateDTO.builder()
                .chatId(stateEntity.getStateEntityId().getChatId())
                .userId(stateEntity.getStateEntityId().getUserId())
                .currentCommand(CommandMapper.getCommandDTO(stateEntity.getCurrentCommand()))
                .previousCommand(CommandMapper.getCommandDTO(stateEntity.getPreviousCommand()))
                .build();
    }

}
