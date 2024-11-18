package baraholkateam.rest.service;

import baraholkateam.rest.model.PreviousState;
import baraholkateam.rest.repository.PreviousStateRepository;
import baraholkateam.util.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис взаимодействия с сущностью "PreviousState".
 */
@Slf4j
@Service
public class PreviousStateService {

    @Autowired
    private PreviousStateRepository previousStateRepository;

    public Command get(Long chatId) {
        Optional<PreviousState> previousStateRepositoryOptional = previousStateRepository.findById(chatId);
        if (previousStateRepositoryOptional.isPresent()) {
            return previousStateRepositoryOptional.get().getState();
        } else {
            log.error("Previous state for chat {} not found!", chatId);
            return null;
        }
    }

    public void put(Long chatId, Command command) {
        previousStateRepository.save(new PreviousState(chatId, command));
    }

}
