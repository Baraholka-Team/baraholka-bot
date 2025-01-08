package baraholkateam.rest.controller;

import baraholkateam.rest.model.AdvertisementEntity;
import baraholkateam.rest.service.AdvertisementService;
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

    @RequestMapping(method = RequestMethod.POST, value = "/my_advertisements",
            headers = {"content-type=application/json"})
    public ResponseEntity<List<AdvertisementEntity>> getUserAdvertisements(@RequestBody TelegramUserInfo userInfo) {
        Long userId = userInfo.id();

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<AdvertisementEntity> advertisementEntities = advertisementService.getUserAdvertisements(userId);

        return new ResponseEntity<>(advertisementEntities, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add_advertisement",
            headers = {"content-type=application/json"})
    public ResponseEntity<HttpStatus> addNewAdvertisement(@RequestBody JsonNode json) {
        TelegramUserInfo userInfo;
        AdvertisementEntity advertisementEntity;

        try {
            userInfo = controllerHelper.getUserInfo(json);
            advertisementEntity = controllerHelper.getAdvertisement(json);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(userInfo.id())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (!controllerHelper.addNewAdvertisement(advertisementEntity, json)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete_advertisement/{message_id}",
            headers = {"content-type=application/json"})
    public ResponseEntity<HttpStatus> deleteAdvertisement(@RequestBody TelegramUserInfo userInfo,
                                                          @PathVariable("message_id") Integer messageId) {
        Long userId = userInfo.id();

        if (userId == null || messageId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(userId)
                || !controllerHelper.isUserMessageOwner(userId, messageId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            controllerHelper.deleteMessage(messageId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        advertisementService.removeAdvertisement(messageId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/search_advertisements",
            headers = {"content-type=application/json"})
    public ResponseEntity<List<AdvertisementEntity>> searchAdvertisements(@RequestBody JsonNode json) {
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
                || !controllerHelper.checkIsUserChannelMember(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String[] allTags = tagsList.toArray(String[]::new);

        List<AdvertisementEntity> advertisementEntities = advertisementService.searchAdvertisementsWithTags(allTags);

        return new ResponseEntity<>(advertisementEntities, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all_tags",
            headers = {"content-type=application/json"})
    public ResponseEntity<AllTags> getAllTags(@RequestBody TelegramUserInfo userInfo) {
        Long userId = userInfo.id();

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!controllerHelper.checkUserRights(userInfo)
                || !controllerHelper.checkIsUserChannelMember(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(new AllTags(), HttpStatus.OK);
    }
}
