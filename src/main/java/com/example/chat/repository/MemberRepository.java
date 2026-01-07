package com.example.chat.repository;

import com.example.chat.models.Member;
import com.example.chat.models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, String> {

    Optional<Member> findById(String id);
    Optional<Member> findByUsername(String id);
}
