package com.example.chat_app_service.authen_service.repository;

import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationUserRepository extends MongoRepository<ApplicationUser, String> {
    ApplicationUser findByUsername(String username);
    ApplicationUser findByEmail(String email);
    List<ApplicationUser> findApplicationUsersByDisplayNameLikeOrderByIdDesc(String name, Pageable pageable);
}