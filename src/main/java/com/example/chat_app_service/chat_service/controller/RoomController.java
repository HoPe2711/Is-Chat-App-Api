package com.example.chat_app_service.chat_service.controller;

import com.example.chat_app_service.chat_service.model.request.CreateRoomRequest;
import com.example.chat_app_service.chat_service.service.RoomService;
import com.example.chat_app_service.response.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@Slf4j
@RestController
@RequestMapping("/auth")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/user_room")
    public ResponseEntity<GeneralResponse<Object>> getRoomByUser(){
        return roomService.getRoomByUser();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<GeneralResponse<Object>> getRoom(@PathVariable(name="roomId") String roomId){
        return roomService.getRoom(roomId);
    }

    @PostMapping("/user_room/{userId}/{roomId}")
    public ResponseEntity<GeneralResponse<Object>> addUserToRoom(@PathVariable(name="userId") String userId,
                                                                 @PathVariable(name = "roomId") String roomId){
        return roomService.addUserToRoom(userId,roomId);
    }

    @PostMapping("/room")
    public ResponseEntity<GeneralResponse<Object>> createRoom(@RequestBody CreateRoomRequest createRoomRequest){
        return roomService.createRoom(createRoomRequest);
    }

    @GetMapping("/find_room/{pattern}/{touch}")
    public ResponseEntity<GeneralResponse<Object>> findRoom(@PathVariable(name="pattern") String pattern,
                                                            @PathVariable(name="touch") int touch){
        return roomService.findRoom(pattern,touch);
    }
}