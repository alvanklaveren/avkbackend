package com.alvanklaveren.rest;

import com.alvanklaveren.ELanguage;
import com.alvanklaveren.usecase.TranslationUseCase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/translation")
public class TranslationController {

    @Autowired
    private TranslationUseCase translationUseCase;

    @CrossOrigin
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

}
