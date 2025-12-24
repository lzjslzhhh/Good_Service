package com.example.goodservice.controller.admin;

import com.example.goodservice.common.Result;
import com.example.goodservice.entity.User;
import com.example.goodservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户（管理员）
     */
    @GetMapping("/list")
    public Result<List<User>> listUsers() {
        List<User> users = userService.listAllUsers();
        // 不返回密码
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /**
     * 根据 userId 查询用户详情
     */
    @GetMapping("/{userId}")
    public Result<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 修改用户信息（手机号、类型等）
     */
    @PutMapping("/{userId}")
    public Result<String> updateUser(@PathVariable Long userId,
                                     @RequestBody User user) {
        userService.updateUser(userId, user);
        return Result.success("用户信息更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public Result<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success("用户删除成功");
    }
}
