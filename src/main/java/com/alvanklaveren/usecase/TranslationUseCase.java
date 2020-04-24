package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.ELanguage;
import com.alvanklaveren.model.Translation;
import com.alvanklaveren.model.TranslationDTO;
import com.alvanklaveren.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TranslationUseCase {

    @Autowired private TranslationRepository translationRepository;


    @Transactional(readOnly=true)
    public String translate(String original, ELanguage eLanguage){

        Translation translation = translationRepository.getByOriginal(original);
        return (translation == null) ? original : eLanguage.translate(translation);
    }

    @Transactional(readOnly=true)
    public Map<String,String> getDictionary(ELanguage eLanguage){

        List<Translation> translations = translationRepository.findAll();

        Map<String,String> translationMap = new HashMap<>();
        for(Translation translation:translations){
            translationMap.put(translation.getOriginal(), eLanguage.translate(translation));
        }

        return translationMap;
    }

    @Transactional(readOnly=true)
    public TranslationDTO getByOriginal(String original){

        Translation translation = translationRepository.getByOriginal(original);
        return TranslationDTO.toDto(translation, 0);
    }
}
