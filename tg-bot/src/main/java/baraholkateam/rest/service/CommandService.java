package baraholkateam.rest.service;

import baraholkateam.exception.BaraholkaBotException;
import baraholkateam.rest.dto.CommandDTO;
import baraholkateam.rest.mapper.CommandMapper;
import baraholkateam.rest.model.CommandEntity;
import baraholkateam.rest.repository.CommandRepository;
import baraholkateam.util.Command;
import baraholkateam.util.Configuration;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис команд бота
 */
@Service
@AllArgsConstructor
public class CommandService {

    private CommandRepository commandRepository;

//    private static final Map<Command, Command> NEXT_COMMAND_MAP = getNextCommandMap();
//    private static final Map<Command, Command> PREVIOUS_COMMAND_MAP = getPreviousCommandMap();

//    private static Map<Command, Command> getNextCommandMap() {
//        return Map.ofEntries(
//                Map.entry(Command.SearchAdvertisements, Command.SearchAdvertisements_AddAdvertisementTypes),
//                Map.entry(Command.SearchAdvertisements_AddAdvertisementTypes, Command.SearchAdvertisements_AddProductCategories),
//                Map.entry(Command.SearchAdvertisements_AddProductCategories, Command.SearchAdvertisements_ShowFoundAdvertisements),
//                Map.entry(Command.NewAdvertisement, Command.NewAdvertisement_AddPhotos),
//                Map.entry(Command.NewAdvertisement_AddPhotos, Command.NewAdvertisement_ConfirmPhoto),
//                Map.entry(Command.NewAdvertisement_ConfirmPhoto, Command.NewAdvertisement_AddDescription),
//                Map.entry(Command.NewAdvertisement_AddDescription, Command.NewAdvertisement_AddCity),
//                Map.entry(Command.NewAdvertisement_AddCity, Command.NewAdvertisement_AddAdvertisementTypes),
//                Map.entry(Command.NewAdvertisement_AddAdvertisementTypes, Command.NewAdvertisement_AddCategories),
//                Map.entry(Command.NewAdvertisement_AddCategories, Command.NewAdvertisement_AddPrice),
//                Map.entry(Command.NewAdvertisement_AddPrice, Command.NewAdvertisement_ConfirmPrice),
//                Map.entry(Command.NewAdvertisement_ConfirmPrice, Command.NewAdvertisement_AddContacts),
//                Map.entry(Command.NewAdvertisement_AddContacts, Command.NewAdvertisement_AddPhone),
//                Map.entry(Command.NewAdvertisement_AddPhone, Command.NewAdvertisement_ConfirmPhone),
//                Map.entry(Command.NewAdvertisement_ConfirmPhone, Command.NewAdvertisement_AddSocial),
//                Map.entry(Command.NewAdvertisement_AddSocial, Command.NewAdvertisement_Confirm)
//        );
//    }

//    private static Map<Command, Command> getPreviousCommandMap() {
//        return Map.ofEntries(
//                Map.entry(Command.Start, Command.Start),
//                Map.entry(Command.Help, Command.MainMenu),
//                Map.entry(Command.MainMenu, Command.MainMenu),
//                Map.entry(Command.UserAdvertisements, Command.MainMenu),
//                Map.entry(Command.NewAdvertisement, Command.MainMenu),
//                Map.entry(Command.DeleteAdvertisement, Command.MainMenu),
//                Map.entry(Command.NewAdvertisement_AddPhotos, Command.NewAdvertisement),
//                Map.entry(Command.NewAdvertisement_ConfirmPhoto, Command.NewAdvertisement_AddPhotos),
//                Map.entry(Command.NewAdvertisement_AddDescription, Command.NewAdvertisement_AddPhotos),
//                Map.entry(Command.NewAdvertisement_AddCity, Command.NewAdvertisement_AddDescription),
//                Map.entry(Command.NewAdvertisement_AddAdvertisementTypes, Command.NewAdvertisement_AddCity),
//                Map.entry(Command.NewAdvertisement_AddCategories, Command.NewAdvertisement_AddCity),
//                Map.entry(Command.NewAdvertisement_AddPrice, Command.NewAdvertisement_AddCity),
//                Map.entry(Command.NewAdvertisement_ConfirmPrice, Command.NewAdvertisement_AddPrice),
//                Map.entry(Command.NewAdvertisement_AddContacts, Command.NewAdvertisement_AddPrice),
//                Map.entry(Command.NewAdvertisement_AddPhone, Command.NewAdvertisement_AddContacts),
//                Map.entry(Command.NewAdvertisement_ConfirmPhone, Command.NewAdvertisement_AddContacts),
//                Map.entry(Command.NewAdvertisement_AddSocial, Command.NewAdvertisement_AddContacts),
//                Map.entry(Command.NewAdvertisement_Confirm, Command.NewAdvertisement_AddContacts),
//                Map.entry(Command.SearchAdvertisements, Command.MainMenu),
//                Map.entry(Command.SearchAdvertisements_AddAdvertisementTypes, Command.SearchAdvertisements),
//                Map.entry(Command.SearchAdvertisements_AddProductCategories, Command.SearchAdvertisements_AddAdvertisementTypes),
//                Map.entry(Command.SearchAdvertisements_ShowFoundAdvertisements, Command.SearchAdvertisements_AddProductCategories)
//        );
//    }
//
//    /**
//     * Возвращает следующую команду
//     * @param currentCommand текущая команда
//     * @return следующую команду, если такая команда существует, иначе бросает {@link BaraholkaBotException}
//     * @throws BaraholkaBotException если следующей команды не существует
//     */
//    public static Command nextCommand(Command currentCommand) throws BaraholkaBotException {
//        Command nextCommand = NEXT_COMMAND_MAP.get(currentCommand);
//        if (nextCommand != null) {
//            return nextCommand;
//        } else {
//            throw new BaraholkaBotException(Configuration.ErrorMessage.NO_NEXT_COMMAND.formatted(currentCommand.getName()));
//        }
//    }

//    /**
//     * Возвращает предыдущую команду
//     * @param currentCommand текущая команда
//     * @return предыдущую команду, если такая команда существует, иначе бросает {@link BaraholkaBotException}
//     * @throws BaraholkaBotException если предыдущей команды не существует
//     */
//    public static Command previousCommand(Command currentCommand) throws BaraholkaBotException {
//        Command previousCommand = PREVIOUS_COMMAND_MAP.get(currentCommand);
//        if (previousCommand != null) {
//            return previousCommand;
//        } else {
//            throw new BaraholkaBotException(Configuration.ExceptionMessage.NO_PREVIOUS_COMMAND.formatted(currentCommand));
//        }
//    }

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
