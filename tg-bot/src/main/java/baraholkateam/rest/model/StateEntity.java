package baraholkateam.rest.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Состояние бота
 */
@Getter
@Setter
@Entity(name = "state")
@Table(name = "state")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateEntity {

    @EmbeddedId
    private StateEntityId stateEntityId;

    @ManyToOne
    @JoinColumn(name = "id")
    private CommandEntity currentCommand;

    @ManyToOne
    @JoinColumn(name = "id")
    private CommandEntity previousCommand;

}
