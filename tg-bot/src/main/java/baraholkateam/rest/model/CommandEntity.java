package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Команда в боте
 */
@Getter
@Setter
@Entity(name = "command")
@Table(name = "command")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "command_sequence")
    @Column(name = "command_id", nullable = false)
    private Long commandId;

    @Column(name = "name", unique = true, length = 64, nullable = false)
    private String name;

    @Column(name = "description", unique = true, length = 256, nullable = false)
    private String description;

    @OneToMany(mappedBy = "currentCommand")
    private List<StateEntity> currentStates;

    @OneToMany(mappedBy = "previousCommand")
    private List<StateEntity> previousStates;

    @OneToOne(mappedBy = "currentCommand")
    private CommandOrderEntity currentCommandOrder;

    @OneToMany(mappedBy = "nextCommand")
    private List<CommandOrderEntity> nextCommandOrders;

}
