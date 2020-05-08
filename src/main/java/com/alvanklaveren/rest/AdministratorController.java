package com.alvanklaveren.rest;

import com.alvanklaveren.AVKConfig;
import com.alvanklaveren.model.ClassificationDTO;
import com.alvanklaveren.model.ConstantsDTO;
import com.alvanklaveren.model.ForumUserDTO;
import com.alvanklaveren.usecase.AdministratorUseCase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins={AVKConfig.crossOrigin})
@RequestMapping("/backend/administrator")
public class AdministratorController {

    @Autowired private AdministratorUseCase administratorUseCase;

    @RequestMapping(value = "/getConstant", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<ConstantsDTO> getConstant(@RequestBody Integer codeConstants) {

        ConstantsDTO constantsDTO = administratorUseCase.getByCode(codeConstants);

        return new ResponseEntity<>(constantsDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getConstantById", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<ConstantsDTO> getConstantById(@RequestBody String codeConstants) {

        ConstantsDTO constantsDTO = administratorUseCase.getById(codeConstants);

        return new ResponseEntity<>(constantsDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveConstant", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<ConstantsDTO> saveConstant(@RequestBody ConstantsDTO constantsDTO) {

        constantsDTO = administratorUseCase.save(constantsDTO);
        return new ResponseEntity<>(constantsDTO, HttpStatus.OK);
    }

    @RequestMapping(value="/uploadConstantsImage", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<ConstantsDTO> uploadConstantImage(@RequestParam("imageFile") MultipartFile file, @RequestParam("codeConstants") Integer codeConstants){

        ConstantsDTO constantsDTO = administratorUseCase.uploadImage(codeConstants, file);

        return new ResponseEntity<>(constantsDTO, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getConstantsImage", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getConstantsImage(@RequestParam Integer codeConstants) {

        return administratorUseCase.getConstantsImage(codeConstants);
    }

    @RequestMapping(value = "/getUsers", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<List<ForumUserDTO>> getUsers() {

        List<ForumUserDTO> userDTOs = administratorUseCase.getUsers();

        // remove the passwords... its not safe to send them to the client(browser) !!
        userDTOs.forEach(dto -> dto.password = "");

        return new ResponseEntity<>(userDTOs, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getClassifications", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json")
    public ResponseEntity<List<ClassificationDTO>> getClassifications() {

        List<ClassificationDTO> classificationDTOs = administratorUseCase.getClassifications();
        return new ResponseEntity<>(classificationDTOs, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/saveUser", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<ForumUserDTO> saveUser(@RequestBody ForumUserDTO forumUserDTO) {

        forumUserDTO = administratorUseCase.saveUser(forumUserDTO);
        return new ResponseEntity<>(forumUserDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteUser", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<String> deleteUser(@RequestBody Integer codeForumUser) {

        boolean isDeleted = administratorUseCase.deleteUser(codeForumUser);

        JSONObject response = new JSONObject();
        response.put("result", isDeleted?"true":"false");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

}
