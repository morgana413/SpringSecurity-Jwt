package org.example.myprojectbackend.entity.dto;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.example.myprojectbackend.entity.BaseData;

/**
 * (Account)表实体类
 *
 * @author makejava
 * @since 2025-06-04 15:12:07
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("account")
public class Account implements BaseData {
@TableId
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String role;

    private Date registertime;
}
