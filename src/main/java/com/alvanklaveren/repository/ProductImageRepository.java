package com.alvanklaveren.repository;

import com.alvanklaveren.model.ProductImage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> getByProduct_Code(Integer productCode, Sort sort);

}
