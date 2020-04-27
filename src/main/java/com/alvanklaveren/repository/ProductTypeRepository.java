package com.alvanklaveren.repository;

import com.alvanklaveren.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, Integer> {

    ProductType getByCode(Integer code);

}
