package org.example.myprojectbackend.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeVO {
    String token;
    String username;
    String role;
    Date expiresAt;
}
