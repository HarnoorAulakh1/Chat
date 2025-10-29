package com.example.chat.config;

import com.example.chat.service.RedisSubscriberChat;
import com.example.chat.service.RedisSubscriberFriendReq;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,String> ob1=new RedisTemplate<>();
        ob1.setConnectionFactory(redisConnectionFactory);
        ob1.setKeySerializer(new StringRedisSerializer());
        ob1.setValueSerializer(new StringRedisSerializer());
        return ob1;
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public RedisMessageListenerContainer containerForChat(RedisConnectionFactory factory,
                                                   MessageListenerAdapter listenerAdapterForChat,
                                                          MessageListenerAdapter listenerAdapterForFriendRequests) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(listenerAdapterForChat, new ChannelTopic("chat"));
        container.addMessageListener(listenerAdapterForFriendRequests, new ChannelTopic("FriendReq"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapterForChat(RedisSubscriberChat subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public MessageListenerAdapter listenerAdapterForFriendRequests(RedisSubscriberFriendReq subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

}
