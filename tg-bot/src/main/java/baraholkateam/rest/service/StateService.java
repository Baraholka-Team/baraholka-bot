package baraholkateam.rest.service;

import baraholkateam.rest.model.StateEntity;
import baraholkateam.rest.repository.StateRepository;
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
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public Command get(Long chatId) {
        Optional<StateEntity> currentStateRepositoryOptional = stateRepository.findById(chatId);
        if (currentStateRepositoryOptional.isPresent()) {
            return currentStateRepositoryOptional.get().getState();
        } else {
            log.error("Current state for chat {} not found!", chatId);
            return null;
        }
    }

    public void put(Long chatId, Command command) {
        stateRepository.save(new StateEntity(chatId, command));
    }

}
