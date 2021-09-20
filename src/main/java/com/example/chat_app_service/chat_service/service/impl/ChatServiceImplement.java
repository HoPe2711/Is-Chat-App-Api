package com.example.chat_app_service.chat_service.service.impl;

import com.example.chat_app_service.authen_service.repository.ApplicationUserRepository;
import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.chat_service.elasticsearch.repository.ChatMessageSearchRepository;
import com.example.chat_app_service.chat_service.model.request.ChatMessageRequest;
import com.example.chat_app_service.chat_service.model.request.DeleteMessagaRequest;
import com.example.chat_app_service.chat_service.model.request.ReactMessageRequest;
import com.example.chat_app_service.chat_service.model.response.DeleteMessageResponse;
import com.example.chat_app_service.chat_service.model.response.ReactMessageResponse;
import com.example.chat_app_service.chat_service.repository.ChatMessageRepository;
import com.example.chat_app_service.chat_service.repository.ChatRoomRepository;
import com.example.chat_app_service.chat_service.repository.entities.ChatMessage;
import com.example.chat_app_service.chat_service.repository.entities.ChatRoom;
import com.example.chat_app_service.chat_service.repository.entities.React;
import com.example.chat_app_service.chat_service.service.ChatService;
import com.example.chat_app_service.chat_service.service.impl.internal.ChatMessageService;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import java.util.Date;
import java.util.List;
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

@Service
@Slf4j
public class ChatServiceImplement implements ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final ChatMessageSearchRepository chatMessageSearchRepository;

    @Autowired
    public ChatServiceImplement(SimpMessagingTemplate messagingTemplate,
        ChatMessageService chatMessageService, ChatRoomRepository chatRoomRepository,
        ChatMessageRepository chatMessageRepository,
        ApplicationUserRepository applicationUserRepository,
        ChatMessageSearchRepository chatMessageSearchRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.chatMessageSearchRepository = chatMessageSearchRepository;
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
          chatRoom.setChatRoomByChatMessage(chatMessage);
          chatRoomRepository.save(chatRoom);
          for (ApplicationUser applicationUser : chatRoom.getUserList()){
              messagingTemplate.convertAndSend(
                      "/queue/" + applicationUser.getId(),
                      chatMessage
              );
          }
    }

    @Override
    public void reactMessage(ReactMessageRequest reactMessageRequest){
        ChatMessage chatMessage = chatMessageRepository.findById(reactMessageRequest.getChatId()).get();
        React react = new React(reactMessageRequest.getSenderId(), reactMessageRequest.getSenderName(), reactMessageRequest.getEmotion());
        React users = chatMessage.getReactList().stream().
                filter(user -> react.getSenderId().equals(user.getSenderId()))
                .findFirst().orElse(null);
        int index = chatMessage.getReactList().indexOf(users);
        if (users == null) chatMessage.getReactList().add(react);
        else if (reactMessageRequest.getEmotion() == null) chatMessage.getReactList().remove(index);
        else chatMessage.getReactList().set(index,react);
        chatMessageRepository.save(chatMessage);
        for (ApplicationUser applicationUser : chatMessage.getChatRoom().getUserList()){
            messagingTemplate.convertAndSend(
                    "/queue/" + applicationUser.getId(),
                    new ReactMessageResponse(reactMessageRequest.getSenderId(),
                            reactMessageRequest.getSenderName(),
                            reactMessageRequest.getChatId(),
                            chatMessage.getChatRoomId(),
                            chatMessage.getChatRoomName(),
                            reactMessageRequest.getEmotion())
            );
        }
    }

    @Override
    public void deleteMessage(DeleteMessagaRequest deleteMessagaRequest) {
        ChatMessage chatMessage = chatMessageRepository.findById(deleteMessagaRequest.getChatId()).get();
        if (chatMessage.getSenderId().equals(deleteMessagaRequest.getSenderId())){
            for (ApplicationUser applicationUser : chatMessage.getChatRoom().getUserList()){
                messagingTemplate.convertAndSend(
                        "/queue/" + applicationUser.getId(),
                        new DeleteMessageResponse(chatMessage.getSenderId(),
                        chatMessage.getSenderName(),
                        chatMessage.getId(),
                        chatMessage.getChatRoomId(),
                        chatMessage.getChatRoomName(),
                        "delete")
                );
            }
            ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId()).get();
            chatRoom.setSenderName(chatMessage.getSenderName());
            chatRoom.setContent("One message is deleted by " + chatMessage.getSenderName());
            chatRoom.setTimestamp(new Date().getTime());
            chatRoomRepository.save(chatRoom);
            chatMessageRepository.delete(chatMessage);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> findChat(String roomId, int touch,String pattern){
        if (!getUser().getChatRoomList().contains(chatRoomRepository.findById(roomId).get()))
            ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_MESSAGE);
        return ResponseFactory.success(chatMessageSearchRepository.findByContent(pattern,roomId,PageRequest.of(touch, 10 )));
    }
}
