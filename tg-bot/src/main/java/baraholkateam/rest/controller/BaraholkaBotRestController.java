package baraholkateam.rest.controller;

import baraholkateam.rest.dto.AdvertisementDTO;
import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.service.AdvertisementService;
import baraholkateam.rest.service.TagService;
import baraholkateam.util.Tag;
import baraholkateam.util.TelegramUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер взаимодействия с клиентами по REST API.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class BaraholkaBotRestController {

    @Autowired
    private BaraholkaBotRestControllerHelper controllerHelper;

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private TagService tagService;

    @RequestMapping(method = RequestMethod.POST, value = "/my_advertisements/{chat_id}",
            headers = {"content-type=application/json"})
    public ResponseEntity<List<AdvertisementDTO>> getUserAdvertisements(@RequestBody TelegramUserInfo userInfo,
                                                                           @PathVariable("chat_id") Long chatId) {
        Long userId = userInfo.id();

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(chatId, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<AdvertisementDTO> advertisementEntities = advertisementService.getUserAdvertisements(chatId, userId);

        return new ResponseEntity<>(advertisementEntities, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add_advertisement/{chat_id}",
            headers = {"content-type=application/json"})
    public ResponseEntity<HttpStatus> addNewAdvertisement(@RequestBody JsonNode json,
                                                          @PathVariable("chat_id") Long chatId) {
        TelegramUserInfo userInfo;
        AdvertisementDTO advertisementDTO;

        try {
            userInfo = controllerHelper.getUserInfo(json);
            advertisementDTO = controllerHelper.getAdvertisement(json);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(chatId, userInfo.id())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (!controllerHelper.addNewAdvertisement(advertisementDTO, json)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete_advertisement/chat_id={chat_id}&message_id={message_id}",
            headers = {"content-type=application/json"})
    public ResponseEntity<HttpStatus> deleteAdvertisement(@RequestBody TelegramUserInfo userInfo,
                                                          @PathVariable("chat_id") Long chatId,
                                                          @PathVariable("message_id") Integer messageId) {
        Long userId = userInfo.id();

        if (userId == null || messageId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(chatId, userId)
                || !controllerHelper.isUserMessageOwner(chatId, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            controllerHelper.deleteMessage(chatId, userId, messageId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        advertisementService.removeAdvertisement(chatId, messageId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/search_advertisements/{chat_id}",
            headers = {"content-type=application/json"})
    public ResponseEntity<List<AdvertisementDTO>> searchAdvertisements(@RequestBody JsonNode json,
                                                                       @PathVariable("chat_id") Long chatId) {
        TelegramUserInfo userInfo;
        List<String> tagsList;

        try {
            userInfo = controllerHelper.getUserInfo(json);
            tagsList = controllerHelper.getTagsList(json);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long userId = userInfo.id();

        if (userId == null || tagsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(chatId, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<TagDTO> allTags = tagsList.stream()
                .map(tag -> tagService.getTagByName(tag))
                .toList();

        List<AdvertisementDTO> advertisementDTOList = advertisementService.searchAdvertisementsWithTags(allTags);

        return new ResponseEntity<>(advertisementDTOList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all_tags/{chat_id}",
            headers = {"content-type=application/json"})
    public ResponseEntity<Tag[]> getAllTags(@RequestBody TelegramUserInfo userInfo,
                                            @PathVariable("chat_id") Long chatId) {
        Long userId = userInfo.id();

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(chatId, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(Tag.values(), HttpStatus.OK);
    }
}
