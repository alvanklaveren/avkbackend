package com.alvanklaveren.usecase.translation;

import com.alvanklaveren.enums.ELanguage;
import com.alvanklaveren.model.Translation;
import com.alvanklaveren.model.TranslationDTO;
import com.alvanklaveren.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("TranslationUseCase")
public class TranslationUseCase {

    @Autowired private final TranslationRepository translationRepository;

    public TranslationUseCase(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    @Transactional(readOnly=true)
    public String translate(String original, ELanguage eLanguage){

        Translation translation = translationRepository.getByOriginal(original);
        return translation == null ? original : eLanguage.translate(translation);
    }

    @Transactional(readOnly=true)
    public Map<String,String> getDictionary(ELanguage eLanguage){

        List<Translation> translations = translationRepository.findAll();

        Map<String, String> translationMap = new HashMap<>();
        translations.forEach(translation -> {
            translationMap.put(translation.getOriginal(), eLanguage.translate(translation));
        });

        return translationMap;
    }

    @Transactional(readOnly=true)
    public List<TranslationDTO> getTranslations(){

        List<Translation> translations = translationRepository.findAll();
        return TranslationDTO.toDto(translations, 0);
    }

}
