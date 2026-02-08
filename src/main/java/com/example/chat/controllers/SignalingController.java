package com.example.chat.controllers;


import com.example.chat.models.Call;
import com.example.chat.models.Ice;
import com.example.chat.models.IceCandidate;
import com.example.chat.models.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/signal")
public class SignalingController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/ice")
    public void exchangeIce(Ice ice){
        //System.out.println("Ice is= "+ice);
        String receiver=ice.getReceiver();
        IceCandidate candidate=ice.getCandidate();
        simpMessagingTemplate.convertAndSendToUser(receiver,"/topic/ice",ice);
    }

    @MessageMapping("/sendIncoming")
    public void incomingCall(Call call){
        String receiver=call.getReceiver();
        System.out.println("Incomeing sent= "+receiver);
        simpMessagingTemplate.convertAndSendToUser(receiver,"/topic/incoming",call);
    }

    @MessageMapping("/action")
    public void incomingAction(Call call){
        String receiver=call.getReceiver();
        System.out.println("Status= "+call.getStatus());
        simpMessagingTemplate.convertAndSendToUser(receiver,"/topic/action",call);
    }

    @MessageMapping("/offer")
    public void sendOffer(Offer offer){
        String receiver=offer.getReceiver(),sdp=offer.getSdp(),type=offer.getType();
        System.out.println("offer= "+receiver);
        simpMessagingTemplate.convertAndSendToUser(receiver,"/topic/offer",offer);
    }

    @MessageMapping("/answer")
    public void sendAnswer(Offer offer){
        String receiver=offer.getReceiver(),answer=offer.getSdp(),type=offer.getType();
        //System.out.println("Answer="+offer);
        simpMessagingTemplate.convertAndSendToUser(receiver,"/topic/answer",offer);
    }
}
