package com.tech.kj.util;


import com.tech.kj.config.cache.service.GatewayCache;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${application.security.jwt.secret-key}")
    private String SIGNING_KEY;
    @Value("${application.security.jwt.expiration}")
    private long TOKEN_VALIDITY;

    @Autowired
    private GatewayCache gatewayCache;

    @Value("${jwt.authorities.key}")
    public String AUTHORITIES_KEY;


    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public  boolean isValidSignature(String authToken){
        boolean isSignatureValid =false;
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(authToken);

            Claims claims = claimsJws.getBody();
            isSignatureValid  =true;
            return isSignatureValid;
        }catch (SecurityException ex){
            return isSignatureValid;
        }
    }
    public boolean isTokenPresentInCache(String token){
        return (long)gatewayCache.getByKeyWithTTl(token)==-2 ? false: true;
    }

    public boolean isInvalid(String token) {
        if(isTokenPresentInCache(token)){
            return true;
        }else if(!isValidSignature(token)){
            return true;
        }else return isTokenExpired(token);
    }
}