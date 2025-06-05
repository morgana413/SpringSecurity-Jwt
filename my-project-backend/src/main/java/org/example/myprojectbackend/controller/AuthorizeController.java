package org.example.myprojectbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.myprojectbackend.entity.RestBean;
import org.example.myprojectbackend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Autowired
    AccountService accountService;


    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam String email, @RequestParam String type, HttpServletRequest request){
        String message = accountService.registerEmailVerifyCode(email, type, request.getRemoteAddr());
        if (message == null){
            return RestBean.success();
        }
        return RestBean.failure(message);
    }
}
