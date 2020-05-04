package com.alvanklaveren.rest;

import com.alvanklaveren.AVKConfig;
import com.alvanklaveren.model.MessageCategoryDTO;
import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.usecase.ForumUseCase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins={AVKConfig.crossOrigin})
@RequestMapping("/backend/forum")
public class ForumController {

    @Autowired
    private ForumUseCase forumUseCase;

//    @RequestMapping(value = "/getProductList", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
//    public ResponseEntity<List<ProductDTO>> getProductList(@RequestBody String request) {

    @RequestMapping(value = "/getHomePageMessages", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getHomePageMessages(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<MessageDTO> messageDtos = forumUseCase.getByCategoryCode(-1, page, pageSize);

        return new ResponseEntity<>(messageDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessage", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<MessageDTO> getMessage(@RequestBody Integer codeMessage) {

        MessageDTO messageDTO = forumUseCase.getMessage(codeMessage);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getReplyMessages", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getReplyMessages(@RequestBody Integer codeMessage) {

        List<MessageDTO> messageDTOs = forumUseCase.getReplyMessages(codeMessage);
        return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAvatar", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getAvatar(@RequestParam int codeForumUser) {

        return forumUseCase.getAvatar(codeForumUser);
    }

    @RequestMapping(value = "/prepareMessage", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<String> prepareMessage(@RequestBody String messageText) {

        String preparedMessageText = forumUseCase.prepareMessage(messageText);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", preparedMessageText);

        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<MessageDTO> save(@RequestBody MessageDTO messageDTO) {

        messageDTO = forumUseCase.save(messageDTO);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<String> delete(@RequestBody Integer codeMessage) {

        forumUseCase.delete(codeMessage);

        JSONObject response = new JSONObject();
        response.put("result", "true");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessageCategories", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<List<MessageCategoryDTO>> getMessageCategories() {

        List<MessageCategoryDTO> categoryDTOs = forumUseCase.getMessageCategories();
        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessageCategory", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<MessageCategoryDTO> getMessageCategory(@RequestBody Integer codeMessageCategory) {

        MessageCategoryDTO categoryDTO = forumUseCase.getMessageCategory(codeMessageCategory);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessageCount", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<Integer> getMessageCount(@RequestBody Integer codeMessageCategory) {

        Integer messageCount = forumUseCase.getMessageCount(codeMessageCategory);

        return new ResponseEntity<>(messageCount, HttpStatus.OK);
    }

    @RequestMapping(value = "/getMessagesByCategory", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getMessagesByCategory(@RequestBody Integer codeMessageCategory) {

        List<MessageDTO> messageDTOs = forumUseCase.getMessagesByCategory(codeMessageCategory);

        return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
    }
}
