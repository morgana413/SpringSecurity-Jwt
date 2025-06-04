package org.example.myprojectbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.myprojectbackend.entity.dto.Account;


/**
 * (Account)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-04 15:12:12
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}

