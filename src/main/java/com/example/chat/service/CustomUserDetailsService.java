package com.example.chat.service;

import com.example.chat.models.Member;
import com.example.chat.repository.MemberRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;
    private final MemberRepository memberRepository;

    public CustomUserDetailsService(UserRepository repo, MemberRepository memberRepository) {
        this.repo = repo;
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> data = memberRepository.findById(username);
        //System.out.println("Username= "+username+" "+data.isPresent() );

        if (data.isPresent()) {
            Member member=data.get();
            return User.withUsername(member.getId())
                    .password("")
                    .build();
        } else {

            com.example.chat.models.User user = repo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().replace("ROLE_", ""))
                    .build();
        }
    }
}

