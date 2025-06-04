package org.example.myprojectbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final String signKey = "morgana";

    public String createJwt(UserDetails userDetails,int id, String username) {
    Algorithm algorithm = Algorithm.HMAC256(signKey);
    return JWT.create()
            //JWT封装的信息
            .withJWTId(UUID.randomUUID().toString())
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
            if(isValidJWT(jwt.getId())) return null;
            Date expiresAt = jwt.getExpiresAt();
            return new Date().after(expiresAt) ? null : jwt;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public boolean invalidateJWT(String headerToken) {
        String token = convertToken(headerToken);
        if (token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(signKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            String jwtId = decodedJWT.getId();
            return deleteJWT(jwtId,decodedJWT.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    public boolean deleteJWT(String uuid,Date expiresAt) {
        if (isValidJWT(uuid)) return false;
        Date now = new Date();
        Long expiresIn = Math.max(expiresAt.getTime() - now.getTime(), 0);
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expiresIn, TimeUnit.MILLISECONDS);
        return true;
    }
    public boolean isValidJWT(String uuid) {
        return stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST+uuid);
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
