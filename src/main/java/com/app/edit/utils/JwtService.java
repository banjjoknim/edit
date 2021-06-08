package com.app.edit.utils;

import com.app.edit.config.BaseException;
import com.app.edit.config.secret.Secret;
import com.app.edit.enums.UserRole;
import com.app.edit.response.user.GetUserInfo;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

import static com.app.edit.config.BaseResponseStatus.*;

@Service
@Slf4j
public class JwtService {

    private final RedisTemplate<String,String> redisTemplate;
    private final GetDateTime getDateTime;

    @Autowired
    public JwtService(RedisTemplate<String,String> redisTemplate, GetDateTime getDateTime) {
        this.redisTemplate = redisTemplate;
        this.getDateTime = getDateTime;
    }

    private final long ACCESS_TOKEN_VALID_TIME = 14 * 24 * 3600 * 1000L;   // 2주

    /**
     * JWT 생성
     * @param userId
     * @return String
     */
    public String createJwt(Long userId, UserRole role) {
        Date now = new Date();
        long curTime = System.currentTimeMillis();
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role",role.name())
                .setIssuedAt(now) //토큰을 만든 시간
                .setExpiration(new Date(curTime + ACCESS_TOKEN_VALID_TIME)) //토큰 만료 시간
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /**
     * Header에서 X-ACCESS-TOKEN 으로 JWT 추출
     * @return String
     */
    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /**
     * Jwt 만료시간 추출
     * @return String
     */
    public Date getExpireDate(String accessToken) throws BaseException {


        try {

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);

            return claims.getBody().getExpiration();

        } catch (ExpiredJwtException exception) {
            log.info("JWT가 만료되었습니다.");
            throw new BaseException(EXPIRED_JWT);
        }catch (Exception e){
            log.info("유효하지 않은 JWT 입니다.");
            throw new BaseException(INVALID_JWT);
        }

    }

    /**
     * JWT에서 userId 및 Role 추출
     * @return int
     * @throws BaseException
     */
    public GetUserInfo getUserInfo() throws BaseException {
        // 1. JWT 추출
        log.debug("JWT 검증 시작");
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        // 1.5 logout 확인
        if(redisTemplate.opsForValue().get(accessToken) != null){
           throw new BaseException(ALREADY_LOGOUT);
       }

        // 2. JWT parsing
        Jws<Claims> claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);

        } catch (ExpiredJwtException exception) {
            log.debug("JWT가 만료되었습니다.");
            throw new BaseException(EXPIRED_JWT);
        }catch (Exception e){
            log.debug("유효하지 않은 JWT 입니다.");
            throw new BaseException(INVALID_JWT);
        }


        // 3. userInfo 추출
        return GetUserInfo.builder()
                .userId((long)claims.getBody().get("userId", Integer.class))
                .role(claims.getBody().get("role", String.class))
                .build();
    }
}
