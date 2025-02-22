package com.sparta.delivery.domain.user.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class UserServiceLoggingAspect {


    // UserService 에서 조회 기능(R)을 제외한 메서드(CUD)에 대한 Point 설정
    // 디비의 변경이 생길 때 마다 로깅확인용
    @Pointcut("execution(* com.sparta.delivery.domain.user.service.UserService.*(..)) && " +
            "!execution(* com.sparta.delivery.domain.user.service.UserService.get*(..))")
    public void userServiceCUDMethods(){}

    @Around("userServiceCUDMethods()")
    public Object logAfterReturning(ProceedingJoinPoint joinPoint) throws Throwable {

        // 실행된 메서드 정보를 가져옴
        Method method = getMethod(joinPoint);
        log.info("===== method name = {} =====", method.getName());

        // 파라미터 가져오기
        Object[] args = joinPoint.getArgs();

        if (args.length == 0) log.info("no parameter");

        for(Object arg : args){
            log.info("parameter type = {}",arg.getClass().getSimpleName());
            log.info("parameter value = {}", arg);
        }


        Object returnObj;
        try{
            // 실제 메서드 실행
            returnObj = joinPoint.proceed();

            log.info("[UserService] return type = {}", returnObj.getClass().getSimpleName());
            log.info("[UserService] return value = {}", returnObj);
        }catch (Exception e){
            log.error("[UserService] Exception occurred during method execution: {} | Error message: {}", method, e.getMessage());

            throw e;
        }

        return returnObj;
    }

    private Method getMethod(ProceedingJoinPoint proceedingJoinPoint){
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return signature.getMethod();
    }


}


