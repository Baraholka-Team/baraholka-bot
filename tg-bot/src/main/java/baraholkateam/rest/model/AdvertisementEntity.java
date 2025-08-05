package baraholkateam.rest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advertisement_sequence")
    @Column(name = "advertisement_id", nullable = false)
    private Long advertisementId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advertisement_advertisement_id")
    private List<PhotoEntity> photos;

    @Column(name = "description", length = 1024)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "advertisement_tag",
            joinColumns = @JoinColumn(name = "advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagEntity> tags;

    @Column(name = "price")
    private Long price;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advertisement_advertisement_id")
    private List<ContactEntity> contacts;

    @Column(name = "creation_time")
    private Long creationTime;

    @Column(name = "next_update_time")
    private Long nextUpdateTime;

    @Column(name = "update_attempt")
    private Integer updateAttempt;

}
