package com.example.chat.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Offer {
    private String sdp;
    private String type;
    private String receiver;
    private String sender;
}
