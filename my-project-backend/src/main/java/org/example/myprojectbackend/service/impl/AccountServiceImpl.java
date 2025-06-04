package org.example.myprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.myprojectbackend.entity.dto.Account;
import org.example.myprojectbackend.mapper.AccountMapper;
import org.example.myprojectbackend.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * (Account)表服务实现类
 *
 * @author makejava
 * @since 2025-06-04 15:12:11
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

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
}
