package com.alvanklaveren.repository;

import com.alvanklaveren.model.GameConsole;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameConsoleRepository extends JpaRepository<GameConsole, Integer> {

    GameConsole getByCode(Integer code, Sort sort);

}
