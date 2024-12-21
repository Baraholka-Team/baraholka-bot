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
 * Тип тега для объявлений
 */
@Getter
@Setter
@Entity(name = "tag_type")
@Table(name = "tag_type")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagTypeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long tagTypeId;

    @Column(name = "name", length = 64, nullable = false)
    private String tagTypeName;

    @OneToMany(mappedBy = "tagType")
    private List<TagEntity> tags;

}
