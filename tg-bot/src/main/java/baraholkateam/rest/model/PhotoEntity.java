package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity(name = "photo")
@Table(name = "photo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoEntity {

    @Id
    @Column(name = "id")
    private Long photoId;

    @Lob
    @Column(name = "photo", length = Integer.MAX_VALUE)
    private String photo;

    @ManyToOne
    @JoinColumn(name = "id")
    private AdvertisementEntity advertisement;

}
