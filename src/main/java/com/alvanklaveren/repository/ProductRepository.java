package com.alvanklaveren.repository;

import com.alvanklaveren.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> getByGameConsole_Code(Integer codeGameConsole, Pageable pageable);

    List<Product> getByGameConsole_CodeAndProductType_Code(Integer codeGameConsole, Integer codeProductType, Pageable pageable);

    @Query("select p from Product p " +
            "join ProductRating pr on pr.product = p " +
            "where pr.code = (select min(pr2.code) from ProductRating pr2 where pr2.product = p and pr2.rating > 0) " +
            "and p.gameConsole.code = :codeGameConsole " +
            "order by pr.rating asc")
    List<Product> getByGameConsole_CodeByRating(Integer codeGameConsole, Pageable pageable);

    @Query("select p from Product p " +
            "join ProductRating pr on pr.product = p " +
            "where pr.code = (select min(pr2.code) from ProductRating pr2 where pr2.product = p and pr2.rating > 0) " +
            "and p.gameConsole.code = :codeGameConsole and p.productType.code = :codeProductType  " +
            "order by pr.rating asc")
    List<Product> getByGameConsole_CodeAndProductType_CodeByRating(Integer codeGameConsole, Integer codeProductType, Pageable pageable);

    @Query("select p from Product p " +
            "join ProductRating pr on pr.product = p " +
            "where pr.code = (select min(pr2.code) from ProductRating pr2 where pr2.product = p and pr2.rating > 0) " +
            "order by pr.rating asc")
//    WHERE pk.expiryDate = (SELECT MAX(ppk.expiryDate) FROM Pack ppk where ppk.product = pk.product) AND
//    pk.expiryDate BETWEEN :start AND :end
    List<Product> getAllProductsByRating(Pageable pageable);

    @Query("select p from Product p")
    List<Product> getAllProducts(Pageable pageable);

}
