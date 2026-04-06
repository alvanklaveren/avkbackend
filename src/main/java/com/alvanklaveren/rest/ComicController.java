package com.alvanklaveren.rest;

import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.usecase.comics.ComicGeneratorService;
import com.alvanklaveren.usecase.forum.ForumMessageUseCase;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/backend/dailies")
public class ComicController {

    private final ComicGeneratorService comicService;
    private final ForumMessageUseCase forumMessageUseCase;

    public ComicController(ComicGeneratorService comicService,
                           ForumMessageUseCase forumMessageUseCase) {
        this.comicService = comicService;
        this.forumMessageUseCase = forumMessageUseCase;
    }

    @GetMapping(value="/latest")
    public ResponseEntity<byte[]> getLatest() {
        byte[] imageBytes = comicService.generateThreeComics();

        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/getDailies", produces = "application/json")
    public ResponseEntity<List<MessageDTO>> getDailies(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<MessageDTO> messageDTOs =
                forumMessageUseCase.getByCategoryCode(-2, page, pageSize, 0);

        return ResponseEntity.ok(messageDTOs);
    }

    @PostMapping("/save")
    public void save(@RequestParam String base64Image) {
        byte[] data = Base64.getDecoder().decode(base64Image.split(",")[1]);
        comicService.saveSelectedComic(data);
    }
}