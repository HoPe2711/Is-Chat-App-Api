package com.example.chat_app_service.chat_service.repository;

import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import com.example.chat_app_service.chat_service.repository.entities.ChatRoom;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
   List<ChatRoom> findChatRoomByUserListContainingAndRoomNameLike(ApplicationUser user,String name, Pageable pageable);
   List<ChatRoom> findChatRoomByUserListContainingOrderByTimestampDesc(ApplicationUser applicationUser);
}
