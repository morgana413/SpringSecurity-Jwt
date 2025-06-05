package org.example.myprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.myprojectbackend.entity.dto.Account;
import org.example.myprojectbackend.mapper.AccountMapper;
import org.example.myprojectbackend.service.AccountService;
import org.example.myprojectbackend.utils.Const;
import org.example.myprojectbackend.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * (Account)表服务实现类
 *
 * @author makejava
 * @since 2025-06-04 15:12:11
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils flowUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByEmailOrName(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或邮箱不存在");
        }
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account findAccountByEmailOrName(String text){
        return this.query().eq("email", text).or()
                .eq("username",text)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String email, String type, String ip) {
        synchronized (ip.intern()) {
            if (verifyLimit(ip)){
                Random random = new Random();
                int code = random.nextInt(900000);
                Map<String, Object> data = Map.of("code", code, "email", email, "type", type);
                amqpTemplate.convertAndSend("mail", data);
                stringRedisTemplate.opsForValue()
                        .set(Const.MAIL_DATA+email,String.valueOf(code),3, TimeUnit.MINUTES);
                return null;
            }
            return "请求频繁，请稍后再试";

        }
    }

    private boolean verifyLimit(String ip){
        String key = Const.MAIL_LIMIT+ip;
        return flowUtils.limitOnceCheck(ip,60);
    }
}
