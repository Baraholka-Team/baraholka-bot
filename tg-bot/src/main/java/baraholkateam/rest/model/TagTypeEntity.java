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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_type_sequence")
    @Column(name = "id", nullable = false)
    private Long tagTypeId;

    @Column(name = "name", unique = true, length = 64, nullable = false)
    private String tagTypeName;

    @OneToMany(mappedBy = "tagType")
    private List<TagEntity> tags;

}
