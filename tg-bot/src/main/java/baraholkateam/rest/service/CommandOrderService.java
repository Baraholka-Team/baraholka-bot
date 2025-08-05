package baraholkateam.rest.service;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.mapper.CommandMapper;
import baraholkateam.rest.model.CommandEntity;
import baraholkateam.rest.model.CommandOrderEntity;
import baraholkateam.rest.repository.CommandOrderRepository;
import baraholkateam.util.Configuration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CommandOrderService {

    private CommandOrderRepository commandOrderRepository;

    /**
     * Получает следующую команду по текущей команде
     * @param currentCommand текущая команда
     * @return следующую команду
     * @throws BaraholkaBotException если для текущей команды не найдена следующая команда
     */
    public CommandDTO getNextCommand(CommandDTO currentCommand) throws BaraholkaBotException {
        CommandEntity currentCommandEntity = CommandMapper.getCommandEntity(currentCommand);
        Optional<CommandOrderEntity> commandOrderOptional = commandOrderRepository.findByCurrentCommand(currentCommandEntity);
        if (commandOrderOptional.isPresent()) {
            return CommandMapper.getCommandDTO(commandOrderOptional.get().getNextCommand());
        } else {
            throw new BaraholkaBotException(Configuration.ErrorMessage.NO_NEXT_COMMAND.formatted(currentCommandEntity.getName()));
        }
    }

}
