package com.alvanklaveren.repository;

import com.alvanklaveren.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Integer> {

    Translation getByOriginal(String original);

    Optional<Translation> findByCode(Integer codeTranslation);
}
