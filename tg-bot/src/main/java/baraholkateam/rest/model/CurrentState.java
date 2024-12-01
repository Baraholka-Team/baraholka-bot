package baraholkateam.rest.model;

import baraholkateam.util.Command;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Текущее состояние бота
 */
@Entity
@Table(name = "current_state")
@NoArgsConstructor
public class CurrentState {

    @Setter
    @Getter
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "state", length = 64)
    private String state;

    public CurrentState(Long chatId, Command command) {
        this.chatId = chatId;
        this.state = command.getIdentifier();
    }

    public Command getState() {
        return Command.findCommand(state);
    }

    public void setState(Command command) {
        this.state = command.getIdentifier();
    }

}
