package com.example.chat.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ice {
    private IceCandidate candidate;
    private String sender;
    private String receiver;
}



