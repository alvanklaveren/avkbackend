package com.alvanklaveren.repository;

import com.alvanklaveren.model.MessageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageCategoryRepository extends JpaRepository<MessageCategory, Integer> {

    List<MessageCategory> findAll();

}
