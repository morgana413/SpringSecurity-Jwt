package org.example.myprojectbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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
    //解析JWT
    public DecodedJWT decodeJWT(String headerToken) {
        String token = convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(signKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            Date expiresAt = jwt.getExpiresAt();
            return new Date().after(expiresAt) ? null : jwt;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,10);
        return calendar.getTime();
    }
    private String convertToken(String token) {
        if(!StringUtils.hasText(token)||!token.startsWith("Bearer ")){
            return null;
        }
        return token.substring(7);
    }

    public UserDetails toUserDetails(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("****")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return claims.get("id").asInt();
    }
}
