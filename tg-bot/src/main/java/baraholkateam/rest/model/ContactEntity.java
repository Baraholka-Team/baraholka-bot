package baraholkateam.rest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Контакт пользователя для объявления
 */
@Getter
@Setter
@Entity(name = "contact")
@Table(name = "contact")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_sequence")
    @Column(name = "contact_id", nullable = false)
    private Long contactId;

    @ManyToOne
    @JoinColumn(name = "contact_type_id", nullable = false)
    private ContactTypeEntity contactType;

    @Column(name = "name", length = 64, nullable = false)
    private String contactName;

    @ManyToOne(cascade = CascadeType.ALL)
    private AdvertisementEntity advertisement;

}
