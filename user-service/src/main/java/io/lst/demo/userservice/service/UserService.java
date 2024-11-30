package io.lst.demo.userservice.service;

import io.lst.demo.userservice.entity.User;
import io.lst.demo.userservice.event.UserRegisteredEvent;
import io.lst.demo.userservice.kafka.KafkaProducerService;
import io.lst.demo.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        User savedUser = userRepository.save(user);

        // 发布用户注册事件
        UserRegisteredEvent event = new UserRegisteredEvent(savedUser.getUsername(), savedUser.getEmail());
        kafkaProducerService.sendUserRegistrationEvent(event);

        return savedUser;
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setPassword(userDetails.getPassword());
                    user.setEmail(userDetails.getEmail());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
