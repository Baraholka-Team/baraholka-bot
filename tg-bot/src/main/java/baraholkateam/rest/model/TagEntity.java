package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "id")
    private Long tagId;

    @ManyToOne
    @JoinColumn(name = "id")
    private TagTypeEntity tagType;

    @Column(name = "name")
    private String tag;

    @ManyToMany(mappedBy = "tags")
    private List<AdvertisementEntity> advertisement;

}
