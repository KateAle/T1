package t1.openSchool.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("@annotation(t1.openSchool.aspect.annotation.LogExecution)")
    public void logMethodEntry(JoinPoint joinPoint) {
        logger.info("Method called: {} with parameters: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(
            pointcut = "@annotation( t1.openSchool.aspect.annotation.LogException)",
            throwing = "ex"
    )
    public void logMethodException(JoinPoint joinPoint, Exception ex) {
        logger.error("Exception in method {}: {} - {}",
                joinPoint.getSignature().getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    @Around("@annotation( t1.openSchool.aspect.annotation.LogExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        logger.info("Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                executionTime);

        return result;
    }

    @Around("@annotation( t1.openSchool.aspect.annotation.LogTracking)")
    public Object logTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("TRACE START: {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());

        logger.debug("Parameters: {}", Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            logger.debug("Result: {}", result);
            return result;
        } catch (Throwable t) {
            logger.error("Error: {}", t.getMessage());
            throw t;
        } finally {
            logger.info("TRACE END: {}.{}()",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName());
        }
    }

    @AfterReturning(
            pointcut = "@annotation( t1.openSchool.aspect.annotation.HandlingResult)",
            returning = "result"
    )
    public void handleResult(JoinPoint joinPoint, Object result) {
        logger.info("Processing result of method {}",
                joinPoint.getSignature().getName());

        if (result != null) {
            logger.info("Result type: {}", result.getClass().getSimpleName());

            if (result instanceof List) {
                logger.info("Items count: {}", ((List<?>) result).size());
            }
        }
    }
}