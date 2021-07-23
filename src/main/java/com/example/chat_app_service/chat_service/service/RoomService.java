package com.example.chat_app_service.chat_service.service;

import com.example.chat_app_service.chat_service.model.request.CreateRoomRequest;
import com.example.chat_app_service.response.GeneralResponse;
import org.springframework.http.ResponseEntity;

public interface RoomService {
    ResponseEntity<GeneralResponse<Object>> getRoomByUser();
    ResponseEntity<GeneralResponse<Object>> getRoom(String roomId);
    ResponseEntity<GeneralResponse<Object>> addUserToRoom(String userId,String roomId);
    ResponseEntity<GeneralResponse<Object>> createRoom(CreateRoomRequest createRoomRequest);
    ResponseEntity<GeneralResponse<Object>> findRoom(String pattern, int touch);
}
