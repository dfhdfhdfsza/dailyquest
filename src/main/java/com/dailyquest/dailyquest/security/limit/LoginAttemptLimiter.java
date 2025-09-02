package com.dailyquest.dailyquest.security.limit;

import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptLimiter {
    private final StringRedisTemplate redis;
    private final LoginLimitProperties props;
    private final UserRepository userRepository;

    //특정 계정의 로그인 실패 횟수를 집계하는 카운터 키
    private  String kAcctFail(String loginId) {return "login:acct:fail:"+loginId;}
    //특정 계정이 락 상태(소프트락/하드락) 인지를 나타내는 키
    private  String kAcctLock(String loginId) {return "login:acct:lock:"+loginId;}
    //특정 IP 주소의 로그인 실패 횟수를 집계하는 카운터 키
    private  String kIpFail(String ip) {return "login:ip:fail:"+ip;}
    //특정 IP가 쿨다운 상태인지 나타내는 키
    private  String kIpLock(String ip) {return "login:ip:lock:"+ip;}


    /** 클라이언트 IP 추출(프록시 환경이면 X-Forwarded-For 고려) */
    public  String extractClientIp(HttpServletRequest req){
        String xff=req.getHeader("X-Forwarded-For");
        if(xff!=null&&!xff.isBlank()) return  xff.split(",")[0].trim();
        String xri=req.getHeader("X-Real-IP");
        if(xri!=null&&!xri.isBlank()) return  xri.trim();
        return  req.getRemoteAddr();
    }



    /** 사전 체크: 계정/Ip 잠금 상태면 즉시 예외 */
    public  void preCheck(String loginId,String ip){

        //Redis에 저장된 IP 락 키의 TTL을 조회.
        int ipRemain=getTtlSeconds(kIpLock(ip));
        if(ipRemain>0){
            throw  new RateLimitException(429,"IP_COOLDOWN","요청이 너무 많습니다. 잠시 후 다시 시도하세요.",ipRemain);
        }

        //계정 잠금(소프트락) TTL 확인
        int acctRemain=getTtlSeconds(kAcctLock(loginId));
        if(acctRemain>0){
            throw  new RateLimitException(423,"LOGIN_LOCKED","로그인이 일시적으로 제한되었습니다.",acctRemain);
        }

        //DB 하드락(LOCKED_UNTIL)도 차단
        Optional<UserEntity>user=userRepository.findByLoginId(loginId);
        user.ifPresent(u->{
            if(u.getLockedUntil()!=null&&u.getLockedUntil().isAfter(Instant.now())){
                int sec=(int)  Math.max(1, Duration.between(Instant.now(), u.getLockedUntil()).getSeconds());
                throw  new RateLimitException(423,"LOGIN_LOCKED","로그인이 일시적으로 제한되었습니다.",Math.max(sec, 1));
            }
        });
    }

    /** 인증 성공 시: 카운터/락 리셋 */
    @Transactional
    public void onSuccess(String loginId,String ip){
        redis.delete(kAcctFail(loginId));
        redis.delete(kAcctLock(loginId));
        redis.delete(kIpFail(ip));
        redis.delete(kIpLock(ip));
        userRepository.findByLoginId(loginId)
                .ifPresent(u->{
                    userRepository.resetFailStats(loginId);
                    if (u.getLockedUntil()!=null&&u.getLockedUntil().isBefore(Instant.now())){
                        userRepository.updateLockedUntil(loginId,null);
                    }
                });
    }

    /** 인증 실패 시: 카운트 증가 -> 임계 도달 시 락 설정(소프트/하드) + IP 제어 */
    @Transactional
    public void onFailure(String loginId,String ip){
        long acctFails=incrWithWindow(kAcctFail(loginId),props.accountWindowSeconds());
        long ipFails=incrWithWindow(kIpFail(ip),props.ipWindowSeconds());

        // DB 실패 통계 업데이트
        userRepository.findByLoginId(loginId)
                .ifPresent(u->userRepository.updateFailStats(loginId,(int)acctFails,Instant.now()));

        // IP 과도 실패 → IP 쿨다운
        if(ipFails>=props.ipThreshold()){
            setLock(kIpLock(ip),props.ipCooldownSeconds());
        }

        // 계정 소프트락
        if(acctFails== props.accountSoftThreshold()){
            setLock(kAcctLock(loginId),props.accountSoftLockSeconds());
        }

        // 계정 하드락(영속)
        if(acctFails>=props.accountHardThreshold()){
            setLock(kAcctLock(loginId),props.accountHardLockSeconds());
            userRepository.updateLockedUntil(loginId,Instant.now().plusSeconds(props.accountHardLockSeconds()));
        }
    }

    /** 고정 윈도우 카운터: 첫 증가 시에만 TTL 부여 */
    private  long incrWithWindow(String key,int windowSec){
        Long v= redis.opsForValue().increment(key);
        if (v!=null&&v==1L){
            redis.expire(key,windowSec,TimeUnit.SECONDS);
        }
        return v==null ? 1L:v;
    }

    private  void setLock(String lockKey,int seconds){
        redis.opsForValue().set(lockKey,"1",Duration.ofSeconds(seconds));
    }

    private int getTtlSeconds(String key) {
        try {
            Long ttl = redis.getExpire(key, TimeUnit.SECONDS);
            return (ttl == null || ttl < 0) ? 0 : ttl.intValue();
        } catch (DataAccessException e) {
            return 0;
        }
    }
}
