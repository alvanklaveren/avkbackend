package com.alvanklaveren.repository;

import com.alvanklaveren.model.ProductRating;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRatingRepository extends JpaRepository<ProductRating, Integer> {

    List<ProductRating> getByProduct_Code(Integer productCode, Sort sort);

    Optional<ProductRating> findByCode(Integer productCode);
}
