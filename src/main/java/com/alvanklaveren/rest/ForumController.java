package com.alvanklaveren.rest;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.ForumUserDTO;
import com.alvanklaveren.model.MessageCategoryDTO;
import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.model.MessageImageDTO;
import com.alvanklaveren.usecase.ForumUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/backend/forum")
@AllArgsConstructor
@Slf4j
public class ForumController {

    @Autowired
    private final ForumUseCase forumUseCase;

    @RequestMapping(value = "/getHomePageMessages", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getHomePageMessages(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<MessageDTO> messageDtos = forumUseCase.getByCategoryCode(-1, page, pageSize, 0);

        return new ResponseEntity<>(messageDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessage", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<MessageDTO> getMessage(@RequestBody Integer codeMessage) {

        MessageDTO messageDTO = forumUseCase.getMessage(codeMessage);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getReplyMessages", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getReplyMessages(@RequestBody Integer codeMessage) {

        List<MessageDTO> messageDTOs = forumUseCase.getReplyMessages(codeMessage);
        return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAvatar", method = {RequestMethod.GET}, produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getAvatar(@RequestParam int codeForumUser) {

        return forumUseCase.getAvatar(codeForumUser);
    }

    @RequestMapping(value = "/prepareMessage", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<String> prepareMessage(@RequestBody String messageText) {

        String preparedMessageText = forumUseCase.prepareMessage(messageText);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", preparedMessageText);

        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @Secured({EClassification.ROLE_MEMBER, EClassification.ROLE_MEMBER})
    @RequestMapping(value = "/save", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<MessageDTO> save(@RequestBody MessageDTO messageDTO) {

        messageDTO = forumUseCase.save(messageDTO);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @RequestMapping(value = "/saveMessageCategory", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<MessageCategoryDTO> saveMessageCategory(@RequestBody MessageCategoryDTO messageCategoryDTO) {

        messageCategoryDTO = forumUseCase.saveMessageCategory(messageCategoryDTO);
        return new ResponseEntity<>(messageCategoryDTO, HttpStatus.OK);
    }

    @Secured({EClassification.ROLE_ADMIN, EClassification.ROLE_MEMBER})
    @RequestMapping(value = "/delete", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> delete(@RequestBody Integer codeMessage) {

        forumUseCase.delete(codeMessage);

        JSONObject response = new JSONObject();
        response.put("result", "true");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @RequestMapping(value = "/deleteMessageCategory", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> deleteMessageCategory(@RequestBody Integer codeMessageCategory) {

        forumUseCase.deleteMessageCategory(codeMessageCategory);

        JSONObject response = new JSONObject();
        response.put("result", "true");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessageCategories", method = {RequestMethod.GET}, produces = "application/json")
    public ResponseEntity<List<MessageCategoryDTO>> getMessageCategories() {

        List<MessageCategoryDTO> categoryDTOs = forumUseCase.getMessageCategories();
        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessageCategory", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<MessageCategoryDTO> getMessageCategory(@RequestBody Integer codeMessageCategory) {

        MessageCategoryDTO categoryDTO = forumUseCase.getMessageCategory(codeMessageCategory);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessageCount", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<Integer> getMessageCount(@RequestBody Integer codeMessageCategory) {

        Integer messageCount = forumUseCase.getMessageCount(codeMessageCategory);

        return new ResponseEntity<>(messageCount, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessagesByCategory", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getMessagesByCategory(@RequestBody Integer codeMessageCategory) {

        List<MessageDTO> messageDTOs = forumUseCase.getMessagesByCategory(codeMessageCategory);

        return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
    }

    @Secured({EClassification.ROLE_ADMIN, EClassification.ROLE_MEMBER})
    @RequestMapping(value="/uploadImage", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<MessageImageDTO> uploadImage(@RequestParam("imageFile") MultipartFile file, @RequestParam("codeMessage") Integer codeMessage){

        MessageImageDTO messageImageDTO = forumUseCase.uploadImage(codeMessage, file);

        return new ResponseEntity<>(messageImageDTO, new HttpHeaders(), HttpStatus.OK);
    }

    @Secured({EClassification.ROLE_ADMIN, EClassification.ROLE_MEMBER})
    @RequestMapping(value="/uploadImageAlt", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<MessageImageDTO> uploadImageAlt(@RequestBody String request){

        JSONObject jsonObject = new JSONObject(request);
        Integer codeMessage = jsonObject.getInt("codeMessage");
        String fileContent = jsonObject.getString("fileContent");

        MessageImageDTO messageImageDTO = forumUseCase.uploadImageAlt(codeMessage, fileContent);

        return new ResponseEntity<>(messageImageDTO, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value="/getImages", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<List<MessageImageDTO>> getImages(){

        List<MessageImageDTO> messageImageDTOs = forumUseCase.getImages();

        return new ResponseEntity<>(messageImageDTOs, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value="/getMessageImage", method = {RequestMethod.GET}, produces=MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getMessageImage(@RequestParam Integer codeMessageImage){

        return forumUseCase.getImage(codeMessageImage);
    }

    @RequestMapping(value = "/emailNewPassword", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<String> emailNewPassword(@RequestBody String username) {

        boolean emailed = forumUseCase.emailNewPassword(username);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", emailed);

        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/getForumUser", method = {RequestMethod.POST}, produces="application/json")
    public @ResponseBody ResponseEntity<ForumUserDTO> getForumUser(@RequestBody String request){

        JSONObject jsonObject = new JSONObject(request);
        Integer code = jsonObject.getInt("code");

        ForumUserDTO forumUserDTO = forumUseCase.getForumUser(code);

        // never reveal password info outside of backend
        forumUserDTO.password = "";

        return new ResponseEntity<>(forumUserDTO, HttpStatus.OK);
    }

}
