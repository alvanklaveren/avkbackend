package com.alvanklaveren.repository;

import com.alvanklaveren.model.Message;
import com.alvanklaveren.projections.MessageListView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("select m from Message m where m.messageCategory.code = :codeMessageCategory and m.message is null")
    List<Message> getByMessageCategory_Code(Integer codeMessageCategory, Pageable pageable);

    @Query("select count(*) from Message m where m.messageCategory.code = :codeMessageCategory and m.message is null")
    Integer countByMessageCategory(Integer codeMessageCategory);

    @Query( """
            select code as code 
            ,      description as description 
            ,      messageDate as messageDate 
            ,      messageCategory.code as messageCategoryCode 
            ,      forumUser.displayName as forumUserName 
            from   Message m 
            where  m.messageCategory.code = :codeMessageCategory 
            and    m.message is null""")
    List<MessageListView> findByMessageCategory_Code(Integer codeMessageCategory, Sort sort);

    List<Message> findByMessage_Code(Integer codeMessage);

    Optional<Message> findByCode(Integer codeMessage);

}
