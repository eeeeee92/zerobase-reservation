package com.zerobase.reservation.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
public class TraceAspect {

    @Around("@annotation(com.zerobase.reservation.global.annotation.Trace)")
    public void doTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        long timeMillis = System.currentTimeMillis();
        log.info("[{}] {} args=[{}]", uuid, joinPoint.getSignature(), args);
        try {
            joinPoint.proceed();
        } catch (Exception exception) {
            throw exception;
        } finally {
            long endTimeMillis = System.currentTimeMillis() - timeMillis;
            log.info("[{}] {} times={}", uuid, joinPoint.getSignature(), endTimeMillis);
        }
    }
}
