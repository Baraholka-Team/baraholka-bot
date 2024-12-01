package baraholkateam.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@Builder
public class AdvertisementEntityId {

    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "owner_chat_id")
    private Long ownerChatId;

}
