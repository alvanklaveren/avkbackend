package com.alvanklaveren.repository;

import com.alvanklaveren.model.RatingUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingUrlRepository extends JpaRepository<RatingUrl, Integer> {

    RatingUrl getByCode(Integer code);
}
