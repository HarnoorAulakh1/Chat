package com.example.chat.service;

import com.example.chat.models.Message;
import com.example.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message getPreview(String sender,String receiver){
        List<Message> messages = messageRepository.findBySenderAndReceiver(
                sender,
                receiver,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        if(messages.size()==0)
            return null;
        return messages.get(0);
    }

}
