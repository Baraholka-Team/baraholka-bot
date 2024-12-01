package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "id")
    private Long contactId;

    @ManyToOne
    @JoinColumn(name = "id")
    private ContactTypeEntity contactType;

    @Column(name = "name")
    private String contactName;

    @ManyToOne
    @JoinColumn(name = "id")
    private AdvertisementEntity advertisement;

}
