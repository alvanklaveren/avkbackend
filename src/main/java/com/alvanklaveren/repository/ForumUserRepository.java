package com.alvanklaveren.repository;

import com.alvanklaveren.model.ForumUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForumUserRepository extends JpaRepository<ForumUser, Integer> {

    ForumUser getByCode(Integer code);

    Optional<ForumUser> findByCode(Integer forumUserCode);
    ForumUser getByUsername(String username);

}
