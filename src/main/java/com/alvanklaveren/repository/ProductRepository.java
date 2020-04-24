package com.alvanklaveren.repository;

import com.alvanklaveren.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> getByGameConsole_Code(Integer codeGameConsole, Pageable pageable);

    List<Product> getByGameConsole_CodeAndProductType_Code(Integer codeGameConsole, Integer codeProductType, Pageable pageable);

}
