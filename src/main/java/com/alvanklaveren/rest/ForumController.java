package com.alvanklaveren.rest;

import com.alvanklaveren.AVKConfig;
import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.usecase.ForumUseCase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @RequestMapping(value = "/getHomePageMessages", method =  {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<MessageDTO>> getHomePageMessages(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<MessageDTO> messageDtos = forumUseCase.getByCategoryCode(-1, page, pageSize);

        return new ResponseEntity<>(messageDtos, HttpStatus.OK);
    }

}
