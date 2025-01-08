package baraholkateam.rest.service;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.dto.StateDTO;
import baraholkateam.rest.mapper.StateMapper;
import baraholkateam.rest.model.StateEntity;
import baraholkateam.rest.model.StateEntityId;
import baraholkateam.rest.repository.StateRepository;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для работы с состояниями бота
 */
@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CommandService commandService;

    /**
     * Получает состояние бота
     * @param chatId id чата
     * @param userId id пользователя
     * @return состояние бота
     * @throws BaraholkaBotException если невозможно получить состояние бота
     */
    public StateDTO getState(@NotNull Long chatId, @NotNull Long userId) throws BaraholkaBotException {
        Optional<StateEntity> stateEntityOptional = stateRepository.findById(new StateEntityId(chatId, userId));
        return stateEntityOptional.isPresent()
                ? StateMapper.getStateDTO(stateEntityOptional.get())
                : null;
    }

    /**
     * Сохраняет состояние бота
     * @param stateDTO состояние бота для сохранения
     */
    public void addState(@NotNull StateDTO stateDTO) {
        stateRepository.save(StateMapper.getStateEntity(stateDTO));
    }

    /**
     * Заменяет текущее состояние бота
     * @param chatId id чата
     * @param userId id пользователя
     * @param currentCommandDTO текущая команда пользователя
     * @throws BaraholkaBotException если невозможно получить состояние бота или состояние не корректное
     */
    public void changeCurrentState(@NotNull Long chatId,
                                   @NotNull Long userId,
                                   @NotNull CommandDTO currentCommandDTO) throws BaraholkaBotException {
        Optional<StateEntity> stateEntityOptional = stateRepository.findById(new StateEntityId(chatId, userId));
        if (stateEntityOptional.isPresent()) {
            StateDTO stateDTO = StateMapper.getStateDTO(stateEntityOptional.get());
            if (stateDTO.getCurrentCommand() != null) {
                stateDTO.setPreviousCommand(stateDTO.getCurrentCommand());
            } else {
                throw new BaraholkaBotException(
                        Configuration.ErrorMessage.NO_CURRENT_STATE_FOUND.formatted(userId)
                );
            }
            stateDTO.setCurrentCommand(currentCommandDTO);
            stateRepository.save(StateMapper.getStateEntity(stateDTO));
        } else {
            CommandDTO previousCommandDTO = commandService.getCommandByName(Command.Start.getName());
            StateDTO newStateDTO = StateDTO.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .currentCommand(currentCommandDTO)
                    .previousCommand(previousCommandDTO)
                    .build();
            stateRepository.save(StateMapper.getStateEntity(newStateDTO));
        }
    }

    /**
     * Удаляет состояние бота по id
     * @param chatId id чата
     * @param userId id пользователя
     */
    public void deleteCurrentState(@NotNull Long chatId, @NotNull Long userId) {
        stateRepository.deleteById(new StateEntityId(chatId, userId));
    }

}
