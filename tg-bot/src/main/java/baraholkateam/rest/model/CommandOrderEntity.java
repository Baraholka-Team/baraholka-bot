package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Порядок команд в боте
 */
@Getter
@Setter
@Entity(name = "command_order")
@Table(name = "command_order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "command_order_sequence")
    @Column(name = "command_order_id", nullable = false)
    private Long commandOrderId;

    @OneToOne
    @JoinColumn(name = "current_command_id")
    private CommandEntity currentCommand;

    @ManyToOne
    @JoinColumn(name = "next_command_id")
    private CommandEntity nextCommand;

}
