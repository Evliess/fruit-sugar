package evliess.io.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
  @Bean
  public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(RateLimiterConfig
                                                                   rateLimiterService) {
    FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new RateLimitFilter(rateLimiterService));
    registrationBean.addUrlPatterns("/public/*", "/private/*");
    registrationBean.setOrder(1);

    return registrationBean;
  }
}
