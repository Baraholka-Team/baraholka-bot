package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @Column(name = "id", nullable = false)
    private Long commandId;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "description", length = 256, nullable = false)
    private String description;

    @OneToMany(mappedBy = "currentCommand")
    private List<StateEntity> currentStates;

    @OneToMany(mappedBy = "previousCommand")
    private List<StateEntity> previousStates;

}
