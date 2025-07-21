package com.example.StageDIP.security;

import com.example.StageDIP.model.User;
import com.example.StageDIP.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // No need to build authorities here if your CustomUserDetails handles it
        return new CustomUserDetails(user);  // THIS IS THE KEY
    }
}
