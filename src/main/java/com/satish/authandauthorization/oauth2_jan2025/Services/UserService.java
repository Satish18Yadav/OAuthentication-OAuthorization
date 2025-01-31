package com.satish.authandauthorization.oauth2_jan2025.Services;


import com.satish.authandauthorization.oauth2_jan2025.Repositories.UserRepository;
import com.satish.authandauthorization.oauth2_jan2025.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }
}
