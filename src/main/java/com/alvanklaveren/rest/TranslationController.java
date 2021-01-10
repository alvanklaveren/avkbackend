package com.alvanklaveren.rest;

import com.alvanklaveren.enums.ELanguage;
import com.alvanklaveren.model.TranslationDTO;
import com.alvanklaveren.usecase.TranslationUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/backend/translation")
@AllArgsConstructor
@Slf4j
public class TranslationController {

    @Autowired
    private final TranslationUseCase translationUseCase;

    @RequestMapping(value = "/translate", method = RequestMethod.POST, produces="application/text")
    public ResponseEntity<String> translate(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String isoA2 = jsonObject.optString("isoA2");
        String original = jsonObject.getString("original");

        ELanguage eLanguage = ELanguage.getByIsoA2(isoA2);
        String translatedText = translationUseCase.translate(original, eLanguage);

        JSONObject response = new JSONObject();
        jsonObject.put("result", translatedText);

        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionary/{isoA2}", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<Map<String,String>> getDictionary(@PathVariable("isoA2") String isoA2){

        if(isoA2 == null || StringUtils.isEmpty(isoA2)){
            isoA2 = "us";
        }

        ELanguage eLanguage = ELanguage.getByIsoA2(isoA2);
        Map<String,String> dictionary = translationUseCase.getDictionary(eLanguage);

        return new ResponseEntity<>(dictionary, HttpStatus.OK);
    }

    @RequestMapping(value = "/getTranslations", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<TranslationDTO>> getTranslations(){

        List<TranslationDTO> translations = translationUseCase.getTranslations();
        return new ResponseEntity<>(translations, HttpStatus.OK);
    }

}
