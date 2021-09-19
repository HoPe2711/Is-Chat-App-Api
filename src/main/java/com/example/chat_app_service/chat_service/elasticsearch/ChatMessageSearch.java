package com.example.chat_app_service.chat_service.elasticsearch;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "appchat1.chatmessage")
public class ChatMessageSearch implements Serializable {
    private static final long serialVersionUID = 5469432507690892571L;
    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String senderId;
    @Field(type = FieldType.Text)
    private String senderName;
    @Field(type = FieldType.Text)
    private String chatRoomId;
    @Field(type = FieldType.Text)
    private String chatRoomName;
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Long)
    private Long timestamp;
}