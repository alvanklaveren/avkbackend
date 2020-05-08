package com.alvanklaveren.repository;

import com.alvanklaveren.model.Constants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConstantsRepository extends JpaRepository<Constants, Integer> {

    Constants getByCode(Integer code);

    List<Constants> getById(String id);
}
