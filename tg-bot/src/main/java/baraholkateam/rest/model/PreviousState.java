package baraholkateam.rest.model;

import baraholkateam.util.State;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Предыдущее состояние бота.
 */
@Entity
@Table(name = "previous_state")
public class PreviousState {

    @Setter
    @Getter
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "state", length = 64)
    private String state;

    public PreviousState(Long chatId, State state) {
        this.chatId = chatId;
        this.state = state.getIdentifier();
    }

    public State getState() {
        return State.findState(state);
    }

    public void setState(State state) {
        this.state = state.getIdentifier();
    }

}
