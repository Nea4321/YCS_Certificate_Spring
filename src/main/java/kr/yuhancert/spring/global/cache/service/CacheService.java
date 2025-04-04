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
     * @param cacheName 캐시 이름
     * @param key 캐시 키
     * @return 캐시된 데이터, 없으면 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String cacheName, Object key) {
        String cacheKey = generateKey(key);

        // 1. 로컬 캐시 확인
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            Cache.ValueWrapper caffeineValue = caffeineCache.get(cacheKey);
            if (caffeineValue != null) {
                log.debug("Local cache hit: {} (local)", cacheName);
                return (T) caffeineValue.get();
            }
        }

        // 2. 분산 캐시 확인
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper redisValue = redisCache.get(cacheKey);
            if (redisValue != null) {
                T result = (T) redisValue.get();
                log.debug("Remote cache hit: {} (remote)", cacheName);

                // 로컬 캐시에도 저장
                if (caffeineCache != null) {
                    caffeineCache.put(cacheKey, result);
                    log.debug("Updated local cache: {}", cacheName);
                }

                return result;
            }
        }

        // 캐시 미스
        log.debug("Cache miss: {}", cacheName);
        return null;
    }

    /**
     * 데이터를 캐시에 저장
     * @param cacheName 캐시 이름
     * @param key 캐시 키
     * @param value 저장할 데이터
     */
    public <T> void put(String cacheName, Object key, T value) {
        if (value == null) {
            log.debug("Skipping null value for cache: {}", cacheName);
            return;
        }

        String cacheKey = generateKey(key);

        // 1. 로컬 캐시에 저장
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.put(cacheKey, value);
            log.debug("Local cached put: {}", cacheName);
        }

        // 2. 분산 캐시에 저장
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.put(cacheKey, value);
            log.debug("Remote Cached put: {}", cacheName);
        }

    }

    /**
     * 캐시에서 데이터 제거
     * @param cacheName 캐시 이름
     * @param key 캐시 키
     */
    public void evict(String cacheName, Object key) {
        String cacheKey = generateKey(key);

        // 1. 로컬 캐시에서 제거
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.evict(cacheKey);
            log.debug("Evicted from local cache: {}", cacheName);
        }

        // 2. 분산 캐시에서 제거
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.evict(cacheKey);
            log.debug("Evicted from remote cache: {}", cacheName);
        }
    }

    /**
     * 캐시 전체 비우기
     * @param cacheName 캐시 이름
     */
    public void clear(String cacheName) {
        // 1. 로컬 캐시 비우기
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.clear();
            log.debug("Cleared local cache: {}", cacheName);
        }

        // 2. 분산 캐시 비우기
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.clear();
            log.debug("Cleared remote cache: {}", cacheName);
        }
    }

    /**
     * 캐시 키 생성
     */
    private String generateKey(Object key) {
        if (key == null) {
            return "null";
        }
        return key.toString();
    }


}
