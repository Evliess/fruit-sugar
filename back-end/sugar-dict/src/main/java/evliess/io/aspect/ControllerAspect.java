package evliess.io.aspect;

import evliess.io.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

  @Pointcut("execution(* evliess.io.controller.*.*(..))")
  public void endpoints() {
  }

  @Around("endpoints()")
  public Object round(ProceedingJoinPoint joinPoint) throws Throwable {
    Object[] args = joinPoint.getArgs();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    String methodName = method.getName();
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
      .getRequestAttributes();
    String path = requestAttributes != null ? requestAttributes.getRequest().getRequestURI() : "unknown";
    Object obj = null;
    long start = System.nanoTime();
    try {
      obj = joinPoint.proceed(args);
      return obj;
    } catch (Throwable throwable) {
      log.error("Endpoint error: {}", throwable.getMessage());
      throw throwable;
    } finally {
      long end = System.nanoTime();
      String elapsed = String.format("%.3f", (end - start) / 1000000.0);
      print(method, methodName, path, obj, JsonUtils.toJsonString(args), elapsed);
    }

  }

  private void print(Method method, String methodName, String path, Object obj, String argsString, String elapsed) {
    log.info("Endpoint: {} {} {}ms args: {}", path, methodName, elapsed, argsString);
  }
}
