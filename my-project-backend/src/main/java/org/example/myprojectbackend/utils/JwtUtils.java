package org.example.myprojectbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String signKey = "morgana";

    public String createJwt(UserDetails userDetails,int id, String username) {
    Algorithm algorithm = Algorithm.HMAC256(signKey);
    return JWT.create()
            //JWT封装的信息
            .withClaim("id",id)
            .withClaim("name",username)
            .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            //JWT过期时间
            .withExpiresAt(this.expireTime())
            //JWT颁发时间
            .withIssuedAt(new Date())
            //签名算法（头部）
            .sign(algorithm);
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,10);
        return calendar.getTime();
    }
}
