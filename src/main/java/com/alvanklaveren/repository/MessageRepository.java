package com.alvanklaveren.repository;

import com.alvanklaveren.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> getByMessageCategory_Code(Integer codeMessageCategory, Pageable pageable);

    @Query("select count(*) from Message m where m.messageCategory.code = :codeMessageCategory")
    Integer countByMessageCategory(Integer codeMessageCategory);

    List<Message> findByMessageCategory_Code(Integer codeMessageCategory, Sort sort);

    List<Message> findByMessage_Code(Integer codeMessage);

}
