package baraholkateam.rest.service;

import baraholkateam.rest.model.CurrentState;
import baraholkateam.rest.repository.CurrentStateRepository;
import baraholkateam.util.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис взаимодействия с сущностью "CurrentState".
 */
@Slf4j
@Service
public class CurrentStateService {

    @Autowired
    private CurrentStateRepository currentStateRepository;

    public Command get(Long chatId) {
        Optional<CurrentState> currentStateRepositoryOptional = currentStateRepository.findById(chatId);
        if (currentStateRepositoryOptional.isPresent()) {
            return currentStateRepositoryOptional.get().getState();
        } else {
            log.error("Current state for chat {} not found!", chatId);
            return null;
        }
    }

    public void put(Long chatId, Command command) {
        currentStateRepository.save(new CurrentState(chatId, command));
    }

}
