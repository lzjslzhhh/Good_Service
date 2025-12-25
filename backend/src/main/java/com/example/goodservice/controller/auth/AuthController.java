package com.example.goodservice.controller.auth;

import com.example.goodservice.common.Result;
import com.example.goodservice.dto.auth.LoginDTO;
import com.example.goodservice.dto.auth.RegisterUserDTO;
import com.example.goodservice.dto.auth.UpdateProfileDTO;
import com.example.goodservice.entity.User;
import com.example.goodservice.service.UserService;
import com.example.goodservice.util.JwtUtil;
import com.example.goodservice.vo.auth.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<LoginUserVO> login(@RequestBody LoginDTO loginDTO) {

        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        // 生成真实的JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
        LoginUserVO vo = new LoginUserVO();
        vo.setId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setuserType(user.getUserType());
        vo.setPhone(user.getPhone());
        vo.setProfile(user.getProfile());
        vo.setToken(token); // 你后续可替换为真实 JWT

        return Result.success(vo);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterUserDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }

    @PutMapping("/profile")
    public Result<String> updateCurrentUserProfile(@RequestHeader(value = "Authorization", required = false) String token,
                                                   @RequestBody UpdateProfileDTO dto) {
        try {
            // 临时实现：使用固定用户ID
            // 从JWT token中获取真实用户ID
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);
            // 创建User对象并设置更新字段
            User user = new User();
            user.setPhone(dto.getPhone());
            user.setProfile(dto.getProfile()); // 注意：intro对应profile字段
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(dto.getPassword()); // 实际应加密
            }

            userService.updateUser(userId, user);

            return Result.success("个人信息更新成功");
        } catch (Exception e) {
            return Result.error("更新个人信息失败: " + e.getMessage());
        }
    }
}
