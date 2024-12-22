package baraholkateam.rest.service;


import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.mapper.CommandMapper;
import baraholkateam.rest.model.CommandEntity;
import baraholkateam.rest.repository.CommandRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис команд бота
 */
@Service
public class CommandService {

    @Autowired
    private CommandRepository commandRepository;

    /**
     * Получает команду по id
     * @param commandId id команды
     * @return команду или null, если такой команды не существует
     * @throws BaraholkaBotException если невозможно получить команду
     */
    public CommandDTO getCommandById(@NotNull Long commandId) throws BaraholkaBotException {
        Optional<CommandEntity> commandEntityOptional = commandRepository.findById(commandId);
        return commandEntityOptional.isPresent()
                ? CommandMapper.getCommandDTO(commandEntityOptional.get())
                : null;
    }

    /**
     * Получает команду по названию
     * @param name название команды
     * @return команду или null, если такой команды не существует
     * @throws BaraholkaBotException если невозможно получить команду
     */
    public CommandDTO getCommandByName(@NotNull String name) throws BaraholkaBotException {
        Optional<CommandEntity> commandEntityOptional = commandRepository.findByName(name);
        return commandEntityOptional.isPresent()
                ? CommandMapper.getCommandDTO(commandEntityOptional.get())
                : null;
    }

    /**
     * Получает команду по описанию
     * @param description описание команды
     * @return команду или null, если такой команды не существует
     * @throws BaraholkaBotException если невозможно получить команду
     */
    public CommandDTO getCommandByDescription(@NotNull String description) throws BaraholkaBotException {
        Optional<CommandEntity> commandEntityOptional = commandRepository.findByDescription(description);
        return commandEntityOptional.isPresent()
                ? CommandMapper.getCommandDTO(commandEntityOptional.get())
                : null;
    }

}
