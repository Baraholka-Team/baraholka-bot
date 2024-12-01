package baraholkateam.rest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Объявление пользователя
 */
@Getter
@Setter
@Entity(name = "advertisement")
@Table(name = "advertisement")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementEntity {

    @EmbeddedId
    private AdvertisementEntityId advertisementEntityId;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.EAGER)
    private List<PhotoEntity> photos;

    @Column(name = "description", length = 1024)
    private String description;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "advertisement_tag",
            joinColumns = { @JoinColumn(name = "advertisement_message_id"), @JoinColumn(name = "advertisement_owner_chat_id") },
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagEntity> tags;

    @Column(name = "price")
    private Long price;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.EAGER)
    private List<ContactEntity> contacts;

    @Column(name = "creation_time")
    private Long creationTime;

    @Column(name = "next_update_time")
    private Long nextUpdateTime;

    @Column(name = "update_attempt")
    private Integer updateAttempt;

}
