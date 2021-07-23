package com.example.chat_app_service.authen_service.security.filter.service;

import com.example.chat_app_service.authen_service.repository.entities.ApplicationUser;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImplement implements UserDetails {
    private static final long serialVersionUID = 4030000316478277980L;
    private final String id;
    private final String username;
    private final String email;

    @JsonIgnore
    private final String password;

    public UserDetailsImplement(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static UserDetailsImplement build(ApplicationUser user) {
        return new UserDetailsImplement(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword());
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
