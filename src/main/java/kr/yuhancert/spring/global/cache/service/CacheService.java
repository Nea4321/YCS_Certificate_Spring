package kr.yuhancert.spring.global.cache.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final CacheManager caffeineCacheManager;
    private final CacheManager redisCacheManager;

    @Autowired
    public CacheService(
            @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
            @Qualifier("redisCacheManager") CacheManager redisCacheManager) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    /**
     * 캐시에서 데이터 조회
     * @param __cacheName 캐시 이름
     * @param __key 캐시 키
     * @return 캐시된 데이터, 없으면 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String __cacheName, Object __key) {
        String cacheKey = generateKey(__key);

        // 1. 로컬 캐시 확인
        Cache caffeineCache = caffeineCacheManager.getCache(__cacheName);
        if (caffeineCache != null) {
            Cache.ValueWrapper caffeineValue = caffeineCache.get(cacheKey);
            if (caffeineValue != null) {
                log.debug("Cache Hit : {} (LOCAL)", __cacheName);
                return (T) caffeineValue.get();
            }
        }

        // 2. 리모트 캐시 확인
        Cache redisCache = redisCacheManager.getCache(__cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper redisValue = redisCache.get(cacheKey);
            if (redisValue != null) {
                T result = (T) redisValue.get();
                log.debug("Cache Hit : {} (REMOTE)", __cacheName);

                // 로컬 캐시에도 저장
                if (caffeineCache != null) {
                    caffeineCache.put(cacheKey, result);
                    log.debug("Updated LOCAL cache : {}", __cacheName);
                }

                return result;
            }
        }

        // 캐시 미스
        log.debug("Cache Miss : {}", __cacheName);
        return null;
    }

    /**
     * 데이터를 캐시에 저장
     * @param __cacheName 캐시 이름
     * @param __key 캐시 키
     * @param __value 저장할 데이터
     */
    public <T> void put(String __cacheName, Object __key, T __value) {

        if (__value == null) {
            log.debug("Skipping null value for cache : {}", __cacheName);
            return;
        }

        String cacheKey = generateKey(__key);

        // 1. 로컬 캐시에 저장
        Cache caffeineCache = caffeineCacheManager.getCache(__cacheName);
        if (caffeineCache != null) {
            caffeineCache.put(cacheKey, __value);
            log.debug("Cached Put : {} (LOCAL)", __cacheName);
        }

        // 2. 분산 캐시에 저장
        Cache redisCache = redisCacheManager.getCache(__cacheName);
        if (redisCache != null) {
            redisCache.put(cacheKey, __value);
            log.debug("Cached Put : {} (REMOTE)", __cacheName);
        }

    }

    /**
     * 캐시에서 데이터 제거
     * @param __cacheName 캐시 이름
     * @param __key 캐시 키
     */
    public void evict(String __cacheName, Object __key) {
        String cacheKey = generateKey(__key);

        // 1. 로컬 캐시에서 제거
        Cache caffeineCache = caffeineCacheManager.getCache(__cacheName);
        if (caffeineCache != null) {
            caffeineCache.evict(cacheKey);
            log.debug("Evicted from local cache : {}", __cacheName);
        }

        // 2. 분산 캐시에서 제거
        Cache redisCache = redisCacheManager.getCache(__cacheName);
        if (redisCache != null) {
            redisCache.evict(cacheKey);
            log.debug("Evicted from remote cache : {}", __cacheName);
        }
    }

    /**
     * 캐시 전체 비우기
     * @param __cacheName 캐시 이름
     */
    public void clear(String __cacheName) {
        // 1. 로컬 캐시 비우기
        Cache caffeineCache = caffeineCacheManager.getCache(__cacheName);
        if (caffeineCache != null) {
            caffeineCache.clear();
            log.debug("Cleared local cache : {}", __cacheName);
        }

        // 2. 분산 캐시 비우기
        Cache redisCache = redisCacheManager.getCache(__cacheName);
        if (redisCache != null) {
            redisCache.clear();
            log.debug("Cleared remote cache : {}", __cacheName);
        }
    }

    /**
     * 캐시 키 생성
     */
    private String generateKey(Object __key) {
        if (__key == null) {
            return "null";
        }
        return __key.toString();
    }


}
