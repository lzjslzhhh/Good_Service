package com.example.goodservice.controller.admin;

import com.example.goodservice.common.PageResult;
import com.example.goodservice.common.Result;
import com.example.goodservice.vo.admin.UserVO;
import com.example.goodservice.dto.admin.UserProfileDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    // ================== 1. 获取用户列表 ==================
    @GetMapping("/users")
    public Result<PageResult<UserVO>> getUserList(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword
    ) {

        // TODO: 后续替换成数据库查询
        List<UserVO> list = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            int index = (page - 1) * pageSize + i + 1;
            UserVO user = new UserVO();
            user.setId((long) index);
            user.setUsername("User_" + index);
            user.setRole(i % 5 == 0 ? "admin" : "user");
            user.setPhone("138" + String.format("%08d", (int)(Math.random() * 100000000)));
            user.setIntro("这是一个普通用户的简介...");
            user.setRating(4.0 + (i % 2) * 0.5); // 示例信用评分
            list.add(user);
        }

        PageResult<UserVO> pageResult = new PageResult<>(list, 45, page, pageSize);
        return Result.success(pageResult);
    }

    // ================== 2. 获取当前用户信息 ==================
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        // TODO: 实际场景根据登录状态查询
        UserVO user = new UserVO();
        user.setId(1L);
        user.setUsername("当前用户");
        user.setPhone("13800000000");
        user.setIntro("这是用户简介");
        user.setRating(4.5);
        user.setRole("user");
        return Result.success(user);
    }

    // ================== 3. 修改当前用户信息 ==================
    @PutMapping("/me")
    public Result<Void> updateCurrentUser(@RequestBody UserProfileDTO dto) {
        // TODO: 更新数据库信息，如果密码为空则不修改
        System.out.println("修改当前用户信息: " + dto);
        return Result.success(null);
    }

    // ================== 4. 管理员修改指定用户（可选） ==================
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserProfileDTO dto) {
        // TODO: 管理员权限，修改指定用户
        System.out.println("管理员修改用户: id=" + id + ", data=" + dto);
        return Result.success(null);
    }
}
