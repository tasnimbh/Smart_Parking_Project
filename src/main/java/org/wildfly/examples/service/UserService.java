package org.wildfly.examples.service;

import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.wildfly.examples.model.User;
import org.wildfly.examples.repository.UserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

public class UserService {

    @Inject
    private UserRepository userRepository;


    @Transactional
    public boolean registerUser(String email, String plainPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false; // Email already exists
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt())); // Hash password
        userRepository.save(user);
        return true;
    }

    public boolean authenticateUser(String email, String plainPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> BCrypt.checkpw(plainPassword, user.getPassword())) // Compare passwords
                .isPresent();
    }
}
