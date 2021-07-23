package com.example.chat_app_service.chat_service.service.impl;

import com.example.chat_app_service.authen_service.repository.ApplicationUserRepository;
import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.chat_service.model.request.CreateRoomRequest;
import com.example.chat_app_service.chat_service.repository.ChatRoomRepository;
import com.example.chat_app_service.chat_service.repository.entities.ChatRoom;
import com.example.chat_app_service.chat_service.service.RoomService;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoomServiceImplement implements RoomService {

    private final ApplicationUserRepository applicationUserRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public RoomServiceImplement(ApplicationUserRepository applicationUserRepository, ChatRoomRepository chatRoomRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    private ApplicationUser getUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationUserRepository.findByUsername(userDetails.getUsername());
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> getRoomByUser(){
        ApplicationUser applicationUser = getUser();
        return ResponseFactory.success(chatRoomRepository.findChatRoomByUserListContainingOrderByTimestampDesc(applicationUser));
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> getRoom(String roomId){
        ApplicationUser applicationUser = getUser();
        if (applicationUser == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.ROOM_EXIST);
        if (chatRoom.getUserList().contains(applicationUser)) return ResponseFactory.success(chatRoom);
        return ResponseFactory.error(HttpStatus.valueOf(403), ResponseStatusEnum.UNKNOWN_ERROR);
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> addUserToRoom(String userId,String roomId){
        ApplicationUser applicationUser = applicationUserRepository.findById(userId).orElse(null);
        if (applicationUser == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.NOT_EXIST);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.ROOM_EXIST);
        if (chatRoom.getUserList().contains(applicationUser))
            return ResponseFactory.error(HttpStatus.valueOf(400), ResponseStatusEnum.USER_EXIST);
        applicationUser.getChatRoomList().add(chatRoom);
        applicationUserRepository.save(applicationUser);
        chatRoom.getUserList().add(applicationUser);
        chatRoomRepository.save(chatRoom);
        return ResponseFactory.success("Add successfully");
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> createRoom(CreateRoomRequest createRoomRequest){
        ApplicationUser applicationUser = getUser();
        ChatRoom chatRoom = new ChatRoom(createRoomRequest,applicationUser);
        chatRoomRepository.save(chatRoom);
        applicationUser.getChatRoomList().add(chatRoom);
        applicationUserRepository.save(applicationUser);
        return ResponseFactory.success(chatRoom);
    }

    @Override
    public ResponseEntity<GeneralResponse<Object>> findRoom(String pattern, int touch){
        Pageable pageable = PageRequest.of(touch, 5 );
        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomByUserListContainingAndRoomNameLike(getUser(), pattern, pageable);
        return ResponseFactory.success(chatRoomList);
    }
}
