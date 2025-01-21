package moe.rtc.transaction.infra.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

public class CacheConfiguration {

    @Value("${application.infra.cache.ttl-minutes}")
    private int expireTtl;

    @Value("${application.infra.cache.cache-size}")
    private int maxSize;

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(expireTtl, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
