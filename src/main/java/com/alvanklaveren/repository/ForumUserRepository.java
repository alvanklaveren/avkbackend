package com.alvanklaveren.repository;

import com.alvanklaveren.model.ForumUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumUserRepository extends JpaRepository<ForumUser, Integer> {

    ForumUser getByCode(Integer code);

}
