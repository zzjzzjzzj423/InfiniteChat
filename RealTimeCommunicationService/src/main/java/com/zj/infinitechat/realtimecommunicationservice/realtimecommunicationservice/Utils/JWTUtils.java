package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.Utils;



import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.Enum.KEY_ENUMS;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.Enum.TIME_OUT_ENUMS;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

import java.time.Duration;
import java.util.Date;

public class JWTUtils {

    private final static Duration expiration=Duration.ofHours(TIME_OUT_ENUMS.JWT_TIMEOUT.getTimeOut());

    public static String generateJWT(long user_id){
        Date expiryDate=new Date(System.currentTimeMillis()+expiration.toMillis());
        return Jwts
                .builder()
                .setSubject(String.valueOf(user_id))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, KEY_ENUMS.JWT_KEY_PAIRS.getValue())
                .compact();
    }


    public static Claims parse(String token) throws JwtException {
        if (token==null){
            throw new JwtException("token 为空");
        }

        return Jwts.parser()
                .setSigningKey(KEY_ENUMS.JWT_KEY_PAIRS.getValue())
                .parseClaimsJws(token)
                .getBody();


    }
}
