package com.alvanklaveren.rest;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.enums.ECodeTable;
import com.alvanklaveren.model.*;
import com.alvanklaveren.usecase.AdministratorUseCase;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alvanklaveren.enums.ECodeTable.*;

@RestController
@RequestMapping("/backend/administrator")
@AllArgsConstructor
@Slf4j
@Secured({EClassification.ROLE_ADMIN})
public class AdministratorController {

    @Autowired private final AdministratorUseCase administratorUseCase;
    @Autowired private final ForumUseCase forumUseCase;

    @PostMapping(value = "/getConstant", produces = "application/json")
    public ResponseEntity<ConstantsDTO> getConstant(@RequestBody Integer codeConstants) {

        ConstantsDTO constantsDTO = administratorUseCase.getByCode(codeConstants);
        return ResponseEntity.ok(constantsDTO);
    }

    @PostMapping(value = "/getConstantById", produces = "application/json")
    public ResponseEntity<ConstantsDTO> getConstantById(@RequestBody String codeConstants) {

        ConstantsDTO constantsDTO = administratorUseCase.getById(codeConstants);
        return ResponseEntity.ok(constantsDTO);
    }

    @PostMapping(value = "/saveConstant", produces="application/json")
    public ResponseEntity<ConstantsDTO> saveConstant(@RequestBody ConstantsDTO constantsDTO) {

        constantsDTO = administratorUseCase.save(constantsDTO);
        return ResponseEntity.ok(constantsDTO);
    }

    @PostMapping(value="/uploadConstantsImage", produces = "application/json")
    public ResponseEntity<ConstantsDTO> uploadConstantImage(@RequestParam("imageFile") MultipartFile file, @RequestParam("codeConstants") Integer codeConstants){

        ConstantsDTO constantsDTO = administratorUseCase.uploadImage(codeConstants, file);
        return ResponseEntity.ok(constantsDTO);
    }

    @PostMapping(value="/uploadConstantsImageAlt", produces = "application/json")
    public ResponseEntity<ConstantsDTO> uploadConstantsImageAlt(@RequestBody String request){

        JSONObject jsonObject = new JSONObject(request);
        Integer codeConstants = jsonObject.getInt("codeConstants");
        String fileContent = jsonObject.getString("fileContent");

        ConstantsDTO constantsDTO = administratorUseCase.uploadImageAlt(codeConstants, fileContent);
        return ResponseEntity.ok(constantsDTO);
    }

    @GetMapping(value = "/getConstantsImage", produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getConstantsImage(@RequestParam Integer codeConstants) {

        return administratorUseCase.getConstantsImage(codeConstants);
    }

    @PostMapping(value = "/getUsers", produces = "application/json")
    public ResponseEntity<List<ForumUserDTO>> getUsers() {

        List<ForumUserDTO> userDTOs = administratorUseCase.getUsers();

        // remove the passwords... it's not safe to send them to the client(browser) !!
        userDTOs.forEach(dto -> dto.password = "");

        return ResponseEntity.ok(userDTOs);
    }

    @PostMapping(value = "/getClassifications", produces = "application/json")
    public ResponseEntity<List<ClassificationDTO>> getClassifications() {

        List<ClassificationDTO> classificationDTOs = administratorUseCase.getClassifications();
        return ResponseEntity.ok(classificationDTOs);
    }

    @PostMapping(value = "/saveUser", produces="application/json")
    public ResponseEntity<ForumUserDTO> saveUser(@RequestBody ForumUserDTO forumUserDTO) {

        boolean isNewUser = (forumUserDTO.code == null || forumUserDTO.code <= 0);

        forumUserDTO = administratorUseCase.saveUser(forumUserDTO);

        // a new user needs to be emailed a new password
        if(isNewUser) {
            forumUseCase.emailNewPassword(forumUserDTO.username);
        }

        return ResponseEntity.ok(forumUserDTO);
    }

    @PostMapping(value = "/deleteUser", produces="application/json")
    public ResponseEntity<String> deleteUser(@RequestBody Integer codeForumUser) {

        Boolean isDeleted = administratorUseCase.deleteUser(codeForumUser);

        JSONObject response = new JSONObject();
        response.put("result", isDeleted.toString());

        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(value = "/saveCodeTableRow", produces="application/json")
    public ResponseEntity<String> saveCodeTable(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);

        ECodeTable eCodeTable = ECodeTable.getByCode(jsonObject.getInt("codeTable"));
        administratorUseCase.saveCodeTable(eCodeTable, jsonObject.get("codeTableRow").toString());

        JSONObject response = new JSONObject();
        response.put("result", Boolean.TRUE.toString());

        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(value = "/deleteCodeTableRow", produces="application/json")
    public ResponseEntity<String> deleteCodeTableRow(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);

        Boolean isDeleted = false;

        int code = jsonObject.getInt("code");
        int codeTable = jsonObject.getInt("codeTable");

        ECodeTable eCodeTable = ECodeTable.getByCode(codeTable);
        switch(eCodeTable){
            case Companies:
                isDeleted = administratorUseCase.deleteCompany(code);
                break;

            case GameConsole:
                isDeleted = administratorUseCase.deleteGameConsole(code);
                break;

            case ProductType:
                isDeleted = administratorUseCase.deleteProductType(code);
                break;

            case RatingUrl:
                isDeleted = administratorUseCase.deleteRatingUrl(code);
                break;

            case Translation:
                isDeleted = administratorUseCase.deleteTranslation(code);
                break;
        }

        JSONObject response = new JSONObject();
        response.put("result", isDeleted.toString());

        return ResponseEntity.ok(response.toString());
    }
}
