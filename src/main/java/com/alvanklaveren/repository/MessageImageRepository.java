package com.alvanklaveren.repository;

import com.alvanklaveren.model.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageImageRepository extends JpaRepository<MessageImage, Integer> {

    MessageImage getByCode(Integer code);

}
