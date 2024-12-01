package baraholkateam.rest.dto;

import baraholkateam.util.Configuration;
import baraholkateam.util.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementDTO implements Serializable {

    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("owner_chat_id")
    private Long ownerChatId;
    @JsonProperty("photos")
    private List<PhotoDTO> photos;
    @JsonProperty("description")
    private String description;
    @JsonProperty("tags")
    private List<TagDTO> tags;
    @JsonProperty("price")
    private Long price;
    @JsonProperty("contacts")
    private List<ContactDTO> contacts;
    @JsonIgnore
    private Long creationTime;
    @JsonIgnore
    private Long nextUpdateTime;
    @JsonIgnore
    private Integer updateAttempt;

    public void addContact(ContactDTO contact) {
        contacts.add(contact);
    }

    public void addContacts(List<ContactDTO> contacts) {
        this.contacts.addAll(contacts);
    }

    public void addPhoto(PhotoDTO photo) {
        photos.add(photo);
    }

    public void addPhotos(List<PhotoDTO> photos) {
        this.photos.addAll(photos);
    }

    public void addTag(TagDTO tag) {
        tags.add(tag);
    }

    public void addTags(List<TagDTO> tags) {
        this.tags.addAll(tags);
    }

    public String getTagsOfType(TagTypeDTO tagType) {
        List<TagDTO> suitableTags = new ArrayList<>();
        for (TagDTO tag : getTags()) {
            if (tag != null && Objects.equals(tag.getTagType(), tagType)) {
                suitableTags.add(tag);
            }
        }
        return suitableTags.stream()
                .map(TagDTO::getTag)
                .map(Tag::getName)
                .reduce(" ", String::concat);
    }

    public String getAdvertisementText() {
        List<TagDTO> tags = getTags();
        Long price = getPrice();

        StringBuilder sb = new StringBuilder();

        sb.append(tags.stream()
                .map(TagDTO::getTag)
                .map(Tag::getName)
                .reduce(" ", String::concat));
        sb.append("\n\n");
        if (price != null) {
            sb.append(String.format(Configuration.Advertisement.PRICE_TEXT, price)).append("\n\n");
        }
        String description = getDescription();
        sb.append(Configuration.Advertisement.DESCRIPTION_TEXT).append(description);
        sb.append("\n");

        List<ContactDTO> contacts = getContacts();
        if (!contacts.isEmpty()) {
            sb.append("-".repeat(50));
            sb.append("\n").append(Configuration.Advertisement.CONTACTS).append("\n");
            List<String> contactsList = contacts.stream()
                    .map(contact -> {
                        switch (contact.getContactType().getContactTypeName()) { // TODO добавить обработку всех типов контактов
                            case Phone -> {
                                return String.format(Configuration.Advertisement.PHONE_NUMBER, contact.getContactName());
                            }
                            default -> {
                                return String.format(Configuration.Advertisement.CONTACT, contact.getContactName());
                            }
                        }
                    })
                    .toList();
            sb.append(contactsList
                    .stream()
                    .reduce(",\n", String::concat));
        }
        return sb.toString();
    }

}
