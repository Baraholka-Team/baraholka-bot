package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Тег для объявлений
 */
@Getter
@Setter
@Entity(name = "tag")
@Table(name = "tag")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_sequence")
    @Column(name = "id", nullable = false)
    private Long tagId;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private TagTypeEntity tagType;

    @Column(name = "name", unique = true, length = 64, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<AdvertisementEntity> advertisements;

    @ManyToMany(mappedBy = "tags")
    private List<ChosenTagsEntity> chosenTags;

}
