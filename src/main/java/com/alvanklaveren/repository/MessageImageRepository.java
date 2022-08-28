package com.alvanklaveren.repository;

import com.alvanklaveren.model.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageImageRepository extends JpaRepository<MessageImage, Integer> {

    MessageImage getByCode(Integer code);

    @Query( """
            select    mi 
            from      MessageImage mi 
            left join Message m on mi.message = m 
            where     (mi.message is not null and m.forumUser.code = :codeForumUser) 
            or        mi.message is null""")
    List<MessageImage> findAll(Integer codeForumUser);

}
