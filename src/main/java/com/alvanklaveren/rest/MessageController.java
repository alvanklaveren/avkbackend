package com.alvanklaveren.rest;

import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.usecase.MessageUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageUseCase messageUseCase;

    @CrossOrigin
    @RequestMapping(value = "/homepage", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<List<MessageDTO>> getHomePageMessages() {

        List<MessageDTO> messageDtos = messageUseCase.getByCategoryCode(-1);

        return new ResponseEntity<>(messageDtos, HttpStatus.OK);
    }

}
