package baraholkateam.rest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Выбранные пользователем теги во время поиска объявлений по тегам
 */
@Getter
@Setter
@Entity(name = "chosen_tags")
@Table(name = "chosen_tags")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChosenTagsEntity {

    @EmbeddedId
    private ChosenTagsEntityId chosenTagsEntityId;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "chosen_tags_tag",
            joinColumns = { @JoinColumn(name = "chosen_tags_chat_id"), @JoinColumn(name = "chosen_tags_message_id") },
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagEntity> tags;

}
