package com.example.chat_app_service.chat_service.service.impl;

import com.example.chat_app_service.authen_service.repository.ApplicationUserRepository;
import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.chat_service.model.request.ChatMessageRequest;
import com.example.chat_app_service.chat_service.repository.ChatMessageRepository;
import com.example.chat_app_service.chat_service.repository.ChatRoomRepository;
import com.example.chat_app_service.chat_service.repository.entities.ChatMessage;
import com.example.chat_app_service.chat_service.repository.entities.ChatRoom;
import com.example.chat_app_service.chat_service.service.ChatService;
import com.example.chat_app_service.chat_service.service.impl.internal.ChatMessageService;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatServiceImplement implements ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public ChatServiceImplement(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, ApplicationUserRepository applicationUserRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.applicationUserRepository = applicationUserRepository;
    }

    private ApplicationUser getUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationUserRepository.findByUsername(userDetails.getUsername());
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> loadChatMessage(String roomId, int touch){
        ApplicationUser applicationUser = getUser();
        if (applicationUser == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.ROOM_EXIST);
        Pageable pageable = PageRequest.of(touch,20);
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatRoom_IdOrderByTimestampDesc(roomId,pageable);
        return ResponseFactory.success(chatMessageList);
    }

    @Override
    public void processMessage(ChatMessageRequest chatMessageRequest) {
          ChatRoom chatRoom = chatRoomRepository.findById(chatMessageRequest.getChatRoomId()).get();
          ChatMessage chatMessage = new ChatMessage(chatMessageRequest,chatRoom);
          chatMessageService.save(chatMessage);
          //System.out.println(chatMessageRequest.getSenderId());
          chatRoom.setChatRoomByChatMessage(chatMessage);
          chatRoomRepository.save(chatRoom);
          for (ApplicationUser applicationUser : chatRoom.getUserList()){
              messagingTemplate.convertAndSendToUser(
                      applicationUser.getId(),"/queue/messages",
                      chatMessage
              );
          }
    }

}
