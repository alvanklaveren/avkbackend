package com.alvanklaveren.repository;

import com.alvanklaveren.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Company getByCode(Integer code);

    @Query("select c from Company c where lower(c.description) like :description")
    List<Company> findByDescription(String description);

}
