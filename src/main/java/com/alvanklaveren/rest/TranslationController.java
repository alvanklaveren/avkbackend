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

    @PostMapping(value = "/translate", produces="application/text")
    public ResponseEntity<String> translate(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String isoA2 = jsonObject.optString("isoA2");
        String original = jsonObject.getString("original");

        ELanguage eLanguage = ELanguage.getByIsoA2(isoA2);
        String translatedText = translationUseCase.translate(original, eLanguage);

        JSONObject response = new JSONObject();
        jsonObject.put("result", translatedText);

        return ResponseEntity.ok(jsonObject.toString());
    }

    @GetMapping(value = "/dictionary/{isoA2}", produces="application/json")
    public ResponseEntity<Map<String,String>> getDictionary(@PathVariable("isoA2") String isoA2){

        isoA2 = isoA2 == null || isoA2.trim().isEmpty() ? "us" : isoA2;

        ELanguage eLanguage = ELanguage.getByIsoA2(isoA2);
        Map<String,String> dictionary = translationUseCase.getDictionary(eLanguage);

        return ResponseEntity.ok(dictionary);
    }

    @GetMapping(value = "/getTranslations", produces="application/json")
    public ResponseEntity<List<TranslationDTO>> getTranslations(){

        List<TranslationDTO> translations = translationUseCase.getTranslations();
        return ResponseEntity.ok(translations);
    }

}
