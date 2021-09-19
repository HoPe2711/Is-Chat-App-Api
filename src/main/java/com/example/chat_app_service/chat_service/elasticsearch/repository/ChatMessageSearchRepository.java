package com.example.chat_app_service.chat_service.elasticsearch.repository;

import com.example.chat_app_service.chat_service.elasticsearch.ChatMessageSearch;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageSearchRepository extends ElasticsearchRepository<ChatMessageSearch, String> {

    @Query("{\"match\": {\"content\": {\"query\": \"?0\"}}}")
    List<ChatMessageSearch> findByContent(String content, Pageable pageable);
}
