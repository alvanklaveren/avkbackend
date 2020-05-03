package com.alvanklaveren.repository;

import com.alvanklaveren.model.Classification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassificationRepository extends JpaRepository<Classification, Integer> {

    Classification getByCode(Integer code);

}
