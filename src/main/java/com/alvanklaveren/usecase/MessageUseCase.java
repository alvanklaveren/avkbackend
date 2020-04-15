package com.alvanklaveren.usecase;

import com.alvanklaveren.model.Message;
import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class MessageUseCase {

    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public List<MessageDTO> getByCategoryCode(Integer codeCategory){

        List<Message> messages = messageRepository.getByMessageCategory_Code(codeCategory);
        return MessageDTO.toDto(messages);
    }

}
