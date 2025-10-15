package com.tarasov.logging;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Aspect
@Component
public class LoggingAspect {

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger LOGGER = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        long startTime = System.currentTimeMillis();
        LOGGER.info("Request: {}; parameters: {}",
                joinPoint.getSignature(),
                Arrays.toString(joinPoint.getArgs()));
        var res = joinPoint.proceed();
        LOGGER.info("Response: {}; execution time: {}",
                res,
                System.currentTimeMillis() - startTime);
        return res;
    }
}
