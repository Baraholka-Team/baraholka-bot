package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * Тип контакта пользователя для объявления
 */
@Getter
@Setter
@Entity(name = "contact_type")
@Table(name = "contact_type")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_type_sequence")
    @Column(name = "contact_type_id", nullable = false)
    private Long contactTypeId;

    @Column(name = "name", unique = true, length = 64, nullable = false)
    private String contactTypeName;

    @OneToMany(mappedBy = "contactType")
    private List<ContactEntity> contacts;

}
