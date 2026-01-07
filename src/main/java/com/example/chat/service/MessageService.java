package com.example.chat.service;

import com.example.chat.models.*;
import com.example.chat.repository.MemberRepository;
import com.example.chat.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private UserService userService;
    @Autowired
    private MemberRepository memberRepository;

    public Message save(Message message){
       return messageRepository.save(message);
    }


    public void push(Message message,String channel) throws JsonProcessingException {
        if(message.getSender()==null || message.getReceiver()==null || message.getContent()==null)
            return;
        Message saved=messageRepository.save(message);
        redisPublisher.publish(channel, RedisMessage.builder().destination("/topic/messages").payload(saved).build());
    }

    public void push(Message message) throws JsonProcessingException {
        if(message.getSender()==null || message.getReceiver()==null || message.getContent()==null)
            return;
        redisPublisher.publish("chat",RedisMessage.builder().destination("/topic/messages").payload(message).build());
    }

    public List<Message> getMessages(String sender,String receiver,String roomId){
        Query query = new Query();
        Optional<User> user1;
        Optional<User> user2;
        if(roomId==null) {
            query.addCriteria(
                    new Criteria().orOperator(
                            new Criteria().andOperator(
                                    Criteria.where("sender").is(sender),
                                    Criteria.where("receiver").is(receiver)
                            ),
                            new Criteria().andOperator(
                                    Criteria.where("sender").is(receiver),
                                    Criteria.where("receiver").is(sender)
                            )
                    )
            );

            query.with(Sort.by(Sort.Direction.ASC, "created_At"));
            List<Message> messages = mongoTemplate.find(query,Message.class);
            if(!messages.isEmpty()) {
                user1 = userService.findById(messages.get(0).getSender());
                user2 = userService.findById(messages.get(0).getReceiver());
                for (Message msg : messages) {
                    if (msg.getSender() != null) {
                        if (user1.isPresent())
                            msg.setSenderEm(user1.get());
                    }
                    if (msg.getReceiver() != null) {
                        if (user2.isPresent())
                            msg.setReceiverEm(user2.get());
                    }
                }
            }
            return messages;
        }
        else{
            query.addCriteria(Criteria.where("roomId").is(roomId));
            List<Message> messages = mongoTemplate.find(query,Message.class);
            if(!messages.isEmpty()) {
                for (Message msg : messages) {
                    if (msg.getSender() != null) {
                        Optional<Member> member=memberRepository.findById(msg.getSender());
                        if (member.isPresent())
                            msg.setSenderEm(User.builder().username(member.get().getUsername()).id(member.get().getId()).email("").password("").name("").build());
                    }
                }
            }
            return messages;
        }
    }

    public Message getPreview(String sender,String receiver){
        Query query = new Query();
        query.addCriteria(
                new Criteria().orOperator(
                        new Criteria().andOperator(
                                Criteria.where("sender").is(sender),
                                Criteria.where("receiver").is(receiver)
                        ),
                        new Criteria().andOperator(
                                Criteria.where("sender").is(receiver),
                                Criteria.where("receiver").is(sender)
                        )
                )
        );

        query.with(Sort.by(Sort.Direction.DESC, "created_At"));

        List<Message> messages = mongoTemplate.find(query,Message.class);
        if(messages.size()==0)
            return null;
        return messages.get(0);
    }

    public long getUnreadCount(String receiver,String sender,String readBy){

        if(readBy.equals(sender)){
            String temp=sender;
            sender=receiver;
            receiver=temp;
        }
        Query query = new Query();

        query.addCriteria(Criteria.where("receiver").is(receiver));
        query.addCriteria(Criteria.where("sender").is(sender));

        Criteria notReadCriteria = Criteria.where("isRead").not().elemMatch(
                Criteria.where("user").is(readBy)
                        .and("readAt").exists(true)
        );

        query.addCriteria(notReadCriteria);

        return mongoTemplate.count(query, Message.class);
    }

    public void markAsRead(String sender,String receiver,String time,String readBy) throws JsonProcessingException {
        time = time.replace(" ", "+");
        System.out.println("time="+time);
        Instant instant = Instant.parse(time);

        Date isoTime= Date.from(instant);
        Query query = new Query();

        query.addCriteria(
                new Criteria().orOperator(
                        new Criteria().andOperator(
                                Criteria.where("sender").is(receiver.equals(readBy)?sender:receiver),
                                Criteria.where("receiver").is(readBy)
                        )
                )
        );

        query.addCriteria(
                Criteria.where("isRead").not().elemMatch(
                        Criteria.where("user").is(readBy)
                                .and("readAt").exists(true)
                )
        );
        query.addCriteria(Criteria.where("created_At").lte(isoTime));

        Update update = new Update().push("isRead",
                Map.of(
                        "user", readBy,
                        "readAt", new Date()
                )
        );

        UpdateResult result=mongoTemplate.updateMulti(query, update, Message.class);
        MarkAsRead markAsRead=MarkAsRead.builder().sender(sender).receiver(receiver).time(time).build();
        if(result.getMatchedCount()>0)
            redisPublisher.publish("chat", RedisMessage.builder().destination("/topic/markAsRead").payload(markAsRead).build());
    }
}

