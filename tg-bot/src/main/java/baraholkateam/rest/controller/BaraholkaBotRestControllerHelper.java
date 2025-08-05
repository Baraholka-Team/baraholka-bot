package baraholkateam.rest.controller;

import baraholkateam.configuration.BaraholkaBotConfiguration;
import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.PhotoDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.ContactService;
import baraholkateam.rest.service.ContactTypeService;
import baraholkateam.rest.service.TagService;
import baraholkateam.telegram_api_requests.TelegramAPIRequests;
import baraholkateam.util.Configuration;
import baraholkateam.util.ContactType;
import baraholkateam.util.Tag;
import baraholkateam.util.TelegramUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class BaraholkaBotRestControllerHelper {

    private ContactService contactService;
    private ContactTypeService contactTypeService;
    private TagService tagService;
    private TelegramAPIRequests telegramAPIRequests;
    private AdvertisementService advertisementService;
    private BaraholkaBotConfiguration baraholkaBotConfiguration;
    private TelegramClient telegramClient;

    boolean checkUserRights(TelegramUserInfo userInfo) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] secretKeyBytes = md.digest(baraholkaBotConfiguration.getToken().getBytes(StandardCharsets.UTF_8));
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
            sha256HMAC.init(secretKey);

            byte[] calculatedHash = Hex.encode(sha256HMAC.doFinal(
                    userInfo
                            .getCheckString()
                            .getBytes(StandardCharsets.UTF_8)
            ));

            return Arrays.equals(calculatedHash, userInfo.hash());
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm", e);
            return false;
        } catch (InvalidKeyException e) {
            log.error("Invalid key", e);
            return false;
        }
    }

    boolean checkIsUserChannelMember(Long chatId, Long userId) {
        String userRole = telegramAPIRequests.getUserRole(chatId, userId);
        return Objects.equals(userRole, "creator")
                || Objects.equals(userRole, "administrator")
                || Objects.equals(userRole, "member");
    }

    TelegramUserInfo getUserInfo(JsonNode json) {
        Long userId = Long.parseLong(json.get("id").asText());
        String firstName = json.get("first_name").asText();
        String lastName = json.get("last_name").asText();
        String username = json.get("username").asText();
        String photoUrl = json.get("photo_url").asText();
        Integer authDate = Integer.parseInt(json.get("auth_date").asText());
        byte[] hash;
        try {
            hash = json.get("hash").binaryValue();
        } catch (IOException e) {
            log.error("Error parsing hash", e);
            return null;
        }

        return new TelegramUserInfo(userId, firstName, lastName, username, photoUrl,
                authDate, hash);
    }

    AdvertisementDTO getAdvertisement(JsonNode json) {
        Iterator<JsonNode> tagNodeIterator = json.withArray("tags").elements();
        List<TagDTO> tags = new ArrayList<>();
        while (tagNodeIterator.hasNext()) {
            tags.add(tagService.getTagByName(tagNodeIterator.next().asText()));
        }
        String phone = json.get("phone").asText();
        if (Objects.equals(phone, "null")) {
            phone = null;
        }
        Iterator<JsonNode> contactsNodeIterator = json.withArray("contacts").elements();
        List<ContactDTO> contactDTOList = new ArrayList<>();
        while (contactsNodeIterator.hasNext()) {
            contactDTOList.add(
                    ContactDTO.builder()
                            .contactType(contactTypeService.getContactTypeByName(ContactType.Social.getName()))
                            .contactName(contactsNodeIterator.next().asText())
                            .build()
            );
        }
        if (phone != null) {
            contactDTOList.add(
                    ContactDTO.builder()
                            .contactName(phone)
                            .contactType(contactTypeService.getContactTypeByName(ContactType.Phone.getName()))
                            .build()
        );
        }
        Long userId = Long.parseLong(json.get("id").asText());
        String description = json.get("description").asText();
        String priceString = json.get("price").asText();
        Long price = null;
        if (!Objects.equals(priceString, "null")) {
            price = Long.parseLong(priceString);
        }

        return AdvertisementDTO.builder()
                .userId(userId)
                .description(description)
                .tags(tags)
                .price(price)
                .contacts(contactDTOList)
                .build();
    }

    List<String> getTagsList(JsonNode json) {
        List<String> tagsList = new ArrayList<>();
        Iterator<JsonNode> tagNodeIterator = json.withArray("tags").elements();
        while (tagNodeIterator.hasNext()) {
            tagsList.add(tagNodeIterator.next().asText());
        }
        return tagsList;
    }

    boolean addNewAdvertisement(AdvertisementDTO advertisementDTO, JsonNode json) {
        if (advertisementDTO.getContacts().isEmpty()) {
            contactService.addContact(
                    ContactDTO.builder()
                            .contactType(contactTypeService.getContactTypeByName(ContactType.Social.getName()))
                            .contactName("@" + telegramAPIRequests.getUser(advertisementDTO.getChatId(), advertisementDTO.getUserId()).username())
                            .build()
            );
        }

        advertisementService.saveNewAdvertisement(advertisementDTO);

        Iterator<JsonNode> photosNodeIterator = json.withArray("photos").elements();
        List<PhotoDTO> photoDTOList = new ArrayList<>();
        while (photosNodeIterator.hasNext()) {
            photoDTOList.add(
                    PhotoDTO.builder()
                            .photo(photosNodeIterator.next().asText())
                            .build()
            );
        }

        Message sentAd;
        if (photoDTOList.size() == 1) {
            try {
                File photoFile = File.createTempFile("photo", "temp");
                Files.write(Path.of(photoFile.getPath()), Base64.getDecoder().decode(photoDTOList.get(0).getPhoto()));
                sentAd = telegramClient.execute(
                        SendPhoto.builder()
                                .chatId(advertisementDTO.getChatId())
                                .photo(new InputFile(photoFile))
                                .caption(advertisementService.getLastUserAdvertisement(advertisementDTO.getChatId(), advertisementDTO.getUserId()).getAdvertisementText())
                                .build()
                );
            } catch (Exception e) {
                log.error("Cannot send photo", e);
                return false;
            }
        } else {
            List<File> photoFiles = new ArrayList<>();

            for (PhotoDTO photoDTO : photoDTOList) {
                try {
                    File file = File.createTempFile("photo", "temp");
                    Files.write(Path.of(file.getPath()), Base64.getDecoder().decode(photoDTO.getPhoto()));
                    photoFiles.add(file);
                } catch (IOException e) {
                    log.error("Cannot send photo", e);
                    return false;
                }
            }

            SendMediaGroup mediaGroup = SendMediaGroup.builder()
                    .chatId(advertisementDTO.getChatId())
                    .medias(photoFiles.stream().map(photoFile -> new InputMediaPhoto(photoFile, photoFile.getName())).toList())
                    .build();

            mediaGroup.getMedias().get(0).setCaption(advertisementService.getLastUserAdvertisement(advertisementDTO.getChatId(), advertisementDTO.getUserId()).getAdvertisementText());
            List<Message> messages;
            try {
                messages = telegramClient.execute(mediaGroup);
            } catch (TelegramApiException e) {
                log.error("Cannot send P", e);
                return false;
            }
            sentAd = messages.get(0);
        }

        if (sentAd != null) {
            advertisementDTO.setMessageId(sentAd.getMessageId());
            advertisementDTO.setPhotos(photoDTOList);
//                    .setCreationTime(System.currentTimeMillis())
//                    .setNextUpdateTime(
//                            System.currentTimeMillis()
//                                    + FIRST_REPEAT_NOTIFICATION_TIME_UNIT
//                                    .toMillis(FIRST_REPEAT_NOTIFICATION_PERIOD)
//                    )
//                    .setUpdateAttempt(0)
            ;
            advertisementService.saveNewAdvertisement(advertisementDTO);

            return true;
        }
        log.error("Cannot send advertisement to channel.");
        return false;
    }

    void deleteMessage(Long chatId, Long userId, Integer messageId) throws TelegramApiException {
        EditMessageCaption editMessage = new EditMessageCaption();
        String adText = advertisementService.getLastUserAdvertisement(chatId, userId).getAdvertisementText()
                .substring(Tag.Actual.getName().length() + 1);
        String editedText = String.format("%s\n\n%s", Configuration.CommandMessage.NOT_ACTUAL_TEXT, adText);
        editMessage.setChatId(chatId);
        editMessage.setMessageId(Integer.parseInt(String.valueOf(messageId)));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setCaption(editedText);

        telegramClient.execute(editMessage);
    }

    boolean isUserMessageOwner(Long chatId, Long userId) {
        AdvertisementDTO advertisementDTO = advertisementService.getLastUserAdvertisement(chatId, userId);
        return advertisementDTO != null && Objects.equals(advertisementDTO.getUserId(), userId);
    }
}
