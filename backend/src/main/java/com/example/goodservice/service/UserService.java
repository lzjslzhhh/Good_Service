package com.example.goodservice.service;

import com.example.goodservice.dto.auth.RegisterUserDTO;
import com.example.goodservice.entity.User;
import com.example.goodservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(RegisterUserDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setUserType(Integer.valueOf(0));
        user.setRegisterTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public void updateUser(Long userId, User newUser) {
        User user = getUserById(userId);

        user.setPhone(newUser.getPhone());
        user.setUserType(newUser.getUserType());
        user.setRealName(newUser.getRealName());
        user.setProfile(newUser.getProfile());
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }
}
