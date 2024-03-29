package com.alvanklaveren.rest;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.ForumUserDTO;
import com.alvanklaveren.model.MessageCategoryDTO;
import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.model.MessageImageDTO;
import com.alvanklaveren.projections.MessageListView;
import com.alvanklaveren.usecase.forum.ForumMessageUseCase;
import com.alvanklaveren.usecase.forum.ForumUserUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ForumMessageUseCase forumMessageUseCase;

    @Autowired
    private final ForumUserUseCase forumUserUseCase;

    @PostMapping(value = "/getHomePageMessages", produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getHomePageMessages(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<MessageDTO> messageDTOs = forumMessageUseCase.getByCategoryCode(-1, page, pageSize, 0);

        return ResponseEntity.ok(messageDTOs);
    }

    @PostMapping(value = "/getMessage", produces = "application/json")
    public ResponseEntity<MessageDTO> getMessage(@RequestBody Integer codeMessage) {

        MessageDTO messageDTO = forumMessageUseCase.getMessage(codeMessage);
        return ResponseEntity.ok(messageDTO);
    }

    @PostMapping(value = "/getReplyMessages", produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getReplyMessages(@RequestBody Integer codeMessage) {

        List<MessageDTO> messageDTOs = forumMessageUseCase.getReplyMessages(codeMessage);
        return ResponseEntity.ok(messageDTOs);
    }

    @GetMapping(value = "/getAvatar", produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getAvatar(@RequestParam int codeForumUser) {

        return forumUserUseCase.getAvatar(codeForumUser);
    }

    @PostMapping(value = "/prepareMessage", produces = "application/json")
    public ResponseEntity<String> prepareMessage(@RequestBody String messageText) {

        String preparedMessageText = forumMessageUseCase.prepareMessage(messageText);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", preparedMessageText);

        return ResponseEntity.ok(jsonObject.toString());
    }

    @Secured({EClassification.ROLE_MEMBER, EClassification.ROLE_ADMIN})
    @PostMapping(value = "/save", produces="application/json")
    public ResponseEntity<MessageDTO> save(@RequestBody MessageDTO messageDTO) {

        messageDTO = forumMessageUseCase.save(messageDTO);
        return ResponseEntity.ok(messageDTO);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/saveMessageCategory", produces="application/json")
    public ResponseEntity<MessageCategoryDTO> saveMessageCategory(@RequestBody MessageCategoryDTO messageCategoryDTO) {

        messageCategoryDTO = forumMessageUseCase.saveMessageCategory(messageCategoryDTO);
        return ResponseEntity.ok(messageCategoryDTO);
    }

    @Secured({EClassification.ROLE_ADMIN, EClassification.ROLE_MEMBER})
    @PostMapping(value = "/delete", produces="application/json")
    public ResponseEntity<String> delete(@RequestBody Integer codeMessage) {

        forumMessageUseCase.delete(codeMessage);

        JSONObject response = new JSONObject();
        response.put("result", Boolean.TRUE.toString());

        return ResponseEntity.ok(response.toString());
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/deleteMessageCategory", produces="application/json")
    public ResponseEntity<String> deleteMessageCategory(@RequestBody Integer codeMessageCategory) {

        forumMessageUseCase.deleteMessageCategory(codeMessageCategory);

        JSONObject response = new JSONObject();
        response.put("result", Boolean.TRUE.toString());

        return ResponseEntity.ok(response.toString());
    }

    @GetMapping(value = "/getMessageCategories", produces = "application/json")
    public ResponseEntity<List<MessageCategoryDTO>> getMessageCategories() {

        List<MessageCategoryDTO> categoryDTOs = forumMessageUseCase.getMessageCategories();
        return ResponseEntity.ok(categoryDTOs);
    }

    @PostMapping(value = "/getMessageCategory", produces = "application/json")
    public ResponseEntity<MessageCategoryDTO> getMessageCategory(@RequestBody Integer codeMessageCategory) {

        MessageCategoryDTO categoryDTO = forumMessageUseCase.getMessageCategory(codeMessageCategory);
        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping(value = "/getMessageCount", produces = "application/json")
    public ResponseEntity<Integer> getMessageCount(@RequestBody Integer codeMessageCategory) {

        Integer messageCount = forumMessageUseCase.getMessageCount(codeMessageCategory);
        return ResponseEntity.ok(messageCount);
    }

    @PostMapping(value = "/getMessagesByCategory", produces = "application/json")
    public ResponseEntity<List<MessageListView>> getMessagesByCategory(@RequestBody Integer codeMessageCategory) {

        List<MessageListView> messageListViews = forumMessageUseCase.getMessagesByCategory(codeMessageCategory);
        return ResponseEntity.ok(messageListViews);
    }

    @Secured({EClassification.ROLE_ADMIN, EClassification.ROLE_MEMBER})
    @PostMapping(value="/uploadImage", produces = "application/json")
    public ResponseEntity<MessageImageDTO> uploadImage(@RequestParam("imageFile") MultipartFile file, @RequestParam("codeMessage") Integer codeMessage){

        MessageImageDTO messageImageDTO = forumMessageUseCase.uploadImage(codeMessage, file);
        return ResponseEntity.ok(messageImageDTO);
    }

    @Secured({EClassification.ROLE_ADMIN, EClassification.ROLE_MEMBER})
    @PostMapping(value="/uploadImageAlt", produces = "application/json")
    public ResponseEntity<MessageImageDTO> uploadImageAlt(@RequestBody String request){

        JSONObject jsonObject = new JSONObject(request);
        Integer codeMessage = jsonObject.getInt("codeMessage");
        String fileContent = jsonObject.getString("fileContent");

        MessageImageDTO messageImageDTO = forumMessageUseCase.uploadImageAlt(codeMessage, fileContent);
        return ResponseEntity.ok(messageImageDTO);
    }

    @PostMapping(value="/getImages", produces = "application/json")
    public ResponseEntity<List<MessageImageDTO>> getImages(){

        List<MessageImageDTO> messageImageDTOs = forumMessageUseCase.getImages();

        return ResponseEntity.ok(messageImageDTOs);
    }

    @GetMapping(value="/getMessageImage", produces=MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getMessageImage(@RequestParam Integer codeMessageImage){

        return forumMessageUseCase.getImage(codeMessageImage);
    }

    @PostMapping(value = "/emailNewPassword", produces = "application/json")
    public ResponseEntity<String> emailNewPassword(@RequestBody String username) {

        boolean emailed = forumUserUseCase.emailNewPassword(username);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", emailed);

        return ResponseEntity.ok(jsonObject.toString());
    }

    @PostMapping(value="/getForumUser", produces="application/json")
    public ResponseEntity<ForumUserDTO> getForumUser(@RequestBody String request){

        JSONObject jsonObject = new JSONObject(request);
        Integer code = jsonObject.getInt("code");

        ForumUserDTO forumUserDTO = forumUserUseCase.getForumUser(code);

        // never reveal password info outside of backend
        forumUserDTO.password = "";

        return ResponseEntity.ok(forumUserDTO);
    }

}
