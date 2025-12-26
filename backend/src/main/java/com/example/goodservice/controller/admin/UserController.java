package com.example.goodservice.controller.admin;

import com.example.goodservice.common.PageResult;
import com.example.goodservice.common.Result;
import com.example.goodservice.entity.User;
import com.example.goodservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.goodservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private UserRepository userRepository ;
    /**
     * 查询所有用户（管理员）
     */
    @GetMapping("/list")
    public Result<PageResult<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), pageSize, Sort.by("id").ascending());
        Page<User> p;
        if (keyword == null || keyword.trim().isEmpty()) {
            p = userRepository.findAll(pageable);
        } else {
            p = userRepository.findByUsernameContainingOrPhoneContaining(keyword, keyword, pageable);
        }

        List<User> list = p.getContent();
        list.forEach(u -> u.setPassword(null));
        PageResult<User> result = new PageResult<>(list, p.getTotalElements(), page, pageSize);
        return Result.success(result);
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
