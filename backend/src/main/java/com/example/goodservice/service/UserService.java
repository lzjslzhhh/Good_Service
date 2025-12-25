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

        // 更新手机号（如果提供）
        if (newUser.getPhone() != null) {
            user.setPhone(newUser.getPhone());
        }

        // 更新用户类型（如果需要管理员修改权限）
        if (newUser.getUserType() != null) {
            user.setUserType(newUser.getUserType());
        }

        // 更新真实姓名（如果提供）
        if (newUser.getRealName() != null) {
            user.setRealName(newUser.getRealName());
        }

        // 更新用户简介（前端传 intro，对应 profile 字段）
        if (newUser.getProfile() != null) {
            user.setProfile(newUser.getProfile());
        }

        // 更新密码（如果提供且不为空）
        if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()) {
            // 使用 BCrypt 加密新密码
            user.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
        }

        // 更新时间戳
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
