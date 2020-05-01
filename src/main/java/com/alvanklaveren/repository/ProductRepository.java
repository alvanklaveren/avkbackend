package com.alvanklaveren.repository;

import com.alvanklaveren.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("select p from Product p " +
            "where (p.gameConsole.code = :codeGameConsole or 0 = :codeGameConsole) " +
            "and (p.productType.code = :codeProductType or 0 = :codeProductType) ")
    List<Product> getByGameConsole_CodeAndProductType_Code(Integer codeGameConsole, Integer codeProductType, Pageable pageable);

    @Query("select p from Product p " +
            "join ProductRating pr on pr.product = p " +
            "where pr.code = (select min(pr2.code) from ProductRating pr2 where pr2.product = p and pr2.rating > 0) " +
            "and (p.gameConsole.code = :codeGameConsole or 0 = :codeGameConsole) " +
            "and (p.productType.code = :codeProductType or 0 = :codeProductType) " +
            "order by pr.rating asc")
    List<Product> getByGameConsole_CodeAndProductType_CodeByRating(Integer codeGameConsole, Integer codeProductType, Pageable pageable);

    @Query("select p from Product p " +
            "where lower(replace(p.name, ' ', '')) like :productName " +
            "or lower(replace(p.name, ' ', '')) like :altProductName " +
            "or lower(replace(p.name, ' ', '')) like :altProductName2 ")
    List<Product> search(String productName, String altProductName, String altProductName2, Pageable pageable);

}
