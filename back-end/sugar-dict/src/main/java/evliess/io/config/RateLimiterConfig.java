package evliess.io.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterConfig {

  // 存储 IP -> Bucket 的映射
  private final Cache<String, Bucket> cache;

  public RateLimiterConfig() {
    // 配置缓存：最大存储 1,000 个 IP，写入后 1 小时过期
    this.cache = Caffeine.newBuilder()
      .maximumSize(1_000)
      .expireAfterAccess(2, TimeUnit.HOURS)
      .build();
  }

  public Bucket resolveBucket(String ip) {
    return cache.get(ip, this::createNewBucket);
  }

  private Bucket createNewBucket(String ip) {
    // 定义规则：每秒 20 个令牌
    Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(1)));
    return Bucket.builder()
      .addLimit(limit)
      .build();
  }
}
