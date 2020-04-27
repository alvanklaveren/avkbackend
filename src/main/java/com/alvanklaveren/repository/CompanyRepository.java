package com.alvanklaveren.repository;

import com.alvanklaveren.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Company getByCode(Integer code);

}
