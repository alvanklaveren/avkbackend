package com.alvanklaveren.repository;

import com.alvanklaveren.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Integer> {

    Translation getByOriginal(String original);

}
