package com.alvanklaveren.rest;

import com.alvanklaveren.enums.ELanguage;
import com.alvanklaveren.usecase.TranslationUseCase;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/backend/translation")
public class TranslationController {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationController.class);

    @Autowired
    private TranslationUseCase translationUseCase;

    @RequestMapping(value = "/translate", method = RequestMethod.POST, produces="application/text")
    public ResponseEntity<String> translate(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String isoA2 = jsonObject.optString("isoA2");
        String original = jsonObject.getString("original");

        ELanguage eLanguage = ELanguage.getByisoA2(isoA2);
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

        ELanguage eLanguage = ELanguage.getByisoA2(isoA2);
        Map<String,String> dictionary = translationUseCase.getDictionary(eLanguage);

        return new ResponseEntity<>(dictionary, HttpStatus.OK);
    }

}
