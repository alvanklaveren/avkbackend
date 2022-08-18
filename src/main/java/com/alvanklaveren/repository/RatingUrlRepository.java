package com.alvanklaveren.repository;

import com.alvanklaveren.model.RatingUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingUrlRepository extends JpaRepository<RatingUrl, Integer> {

    RatingUrl getByCode(Integer code);

    Optional<RatingUrl> findByCode(Integer ratingURLCode);
}
