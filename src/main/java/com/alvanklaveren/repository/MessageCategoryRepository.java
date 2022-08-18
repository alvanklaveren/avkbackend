package com.alvanklaveren.repository;

import com.alvanklaveren.model.MessageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageCategoryRepository extends JpaRepository<MessageCategory, Integer> {
    List<MessageCategory> findAll();

    Optional<MessageCategory> findByCode(Integer codeMessageCategory);

}
