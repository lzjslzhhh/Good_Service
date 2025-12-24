package com.example.goodservice.controller.auth;

import com.example.goodservice.common.Result;
import com.example.goodservice.dto.auth.RegisterUserDTO;
import com.example.goodservice.entity.User;
import com.example.goodservice.service.UserService;
import com.example.goodservice.vo.auth.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginUserVO> login(@RequestParam String username,
                                     @RequestParam String password) {

        User user = userService.login(username, password);

        LoginUserVO vo = new LoginUserVO();
        vo.setId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setuserType(user.getUserType());
        vo.setToken("jwt-token"); // 你后续可替换为真实 JWT

        return Result.success(vo);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterUserDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }
}
