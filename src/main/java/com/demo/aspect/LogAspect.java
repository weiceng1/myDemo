package com.demo.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {


    @Pointcut("@annotation(com.demo.aspect.EnableAutoLog)")
    public void logPointCut() {
        //do nothing
    }

    @Around("logPointCut() && @annotation(autoLogConfig)")
    public Object around(ProceedingJoinPoint joinPoint, com.demo.aspect.EnableAutoLog autoLogConfig) throws Throwable {

        String clazzName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("AutoLog do {}.{}", clazzName, methodName);

        Object result = null;
        Object[] args = joinPoint.getArgs();
        if(autoLogConfig.printParams()) {
            log.info("AutoLog do {}.{} with params [{}]", clazzName, methodName, args);
        }
        else {
            log.info("AutoLog do {}.{}", clazzName, methodName);
        }
        try{
            result = joinPoint.proceed(args);
            if(autoLogConfig.printResult()) {
                log.info("AutoLog do {}.{} result {}", clazzName, methodName, result);
            }
        }
        catch (Exception e) {
            log.error("AutoLog catch exception, do {}.{}", clazzName, methodName, e);
            if(autoLogConfig.throwException()) {
                throw e;
            }
        }
        return result;
    }

}
