package evliess.io.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static evliess.io.controller.Constants.SERVLET_CONTEXT_PATH;

public class RateLimitFilter implements Filter {
  private final RateLimiterConfig rateLimiterService;
  private final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

  public RateLimitFilter(RateLimiterConfig rateLimiterService) {
    this.rateLimiterService = rateLimiterService;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
    throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String requestUri = request.getRequestURI();
    if (requestUri.startsWith(SERVLET_CONTEXT_PATH + "/public") || requestUri.startsWith(SERVLET_CONTEXT_PATH + "/private")) {
      String clientIp = getClientIp(request);
      Bucket bucket = rateLimiterService.resolveBucket(clientIp);
      ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
      if (probe.isConsumed()) {
        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        filterChain.doFilter(request, response);
      } else {
        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.setStatus(429);
        logger.warn("This ip is attacking: {}", clientIp);
        response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  // 简单的获取 IP 方法，生产环境建议处理 X-Forwarded-For
  private String getClientIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
