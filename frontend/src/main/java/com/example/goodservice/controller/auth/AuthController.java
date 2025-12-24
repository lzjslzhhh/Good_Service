package com.example.goodservice.controller.auth;
import com.example.goodservice.dto.auth.RegisterUserDTO;
import com.example.goodservice.common.Result;
import com.example.goodservice.vo.auth.LoginUserVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<LoginUserVO> login(@RequestParam String username, @RequestParam String password) {

        // TODO: 真实情况需要校验数据库用户名密码
        LoginUserVO user = new LoginUserVO();
        user.setId(1L);
        user.setUsername(username);
        user.setRole("admin".equals(username) ? "admin" : "user");
        user.setToken("jwt-token"); // 模拟 token，真实环境需生成 JWT

        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterUserDTO dto) {
        // TODO: 检查用户名是否存在
        // TODO: 密码加密
        // TODO: 保存到数据库

        return Result.success("注册成功");
    }
}
