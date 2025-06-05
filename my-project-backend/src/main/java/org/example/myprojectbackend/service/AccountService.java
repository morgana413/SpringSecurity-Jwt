package org.example.myprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.myprojectbackend.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * (Account)表服务接口
 *
 * @author makejava
 * @since 2025-06-04 15:12:09
 */
public interface AccountService extends IService<Account>, UserDetailsService {
        String registerEmailVerifyCode(String email,String type,String ip);
}
