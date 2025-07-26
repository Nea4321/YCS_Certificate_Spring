package kr.yuhancert.spring.global.cache.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final CacheManager caffeineCacheManager;
    private final CacheManager redisCacheManager;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheService(
            @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
            @Qualifier("redisCacheManager") CacheManager redisCacheManager,
            ObjectMapper objectMapper) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCacheManager = redisCacheManager;
        this.objectMapper = objectMapper;
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
                Object rawResult = redisValue.get();
                log.debug("Cache Hit : {} (REMOTE)", __cacheName);

                // 로컬 캐시에도 저장
                if (caffeineCache != null) {
                    caffeineCache.put(cacheKey, rawResult);
                    log.debug("Updated LOCAL cache : {}", __cacheName);
                }

                return (T) rawResult;
            }
        }

        // 캐시 미스
        log.debug("Cache Miss : {}", __cacheName);
        return null;
    }

    /**
     * 타입 안전한 캐시 조회 (LinkedHashMap 자동 변환)
     * @param __cacheName 캐시 이름
     * @param __key 캐시 키
     * @param __type 목표 타입
     * @return 캐시된 데이터, 없으면 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String __cacheName, Object __key, Class<T> __type) {
        Object result = get(__cacheName, __key);
        if (result == null) {
            return null;
        }

        // 이미 올바른 타입인 경우
        if (__type.isInstance(result)) {
            return __type.cast(result);
        }

        // LinkedHashMap인 경우 ObjectMapper로 변환
        if (result instanceof LinkedHashMap && objectMapper != null) {
            try {
                T converted = objectMapper.convertValue(result, __type);
                log.debug("Successfully converted LinkedHashMap to {}", __type.getSimpleName());
                return converted;
            } catch (Exception e) {
                log.warn("Failed to convert LinkedHashMap to {}: {}", __type.getSimpleName(), e.getMessage());
                return null;
            }
        }

        log.warn("Cannot convert {} to {}", result.getClass().getSimpleName(), __type.getSimpleName());
        return null;
    }

    /**
     * 복잡한 타입을 위한 캐시 조회 (List, Map 등)
     * @param __cacheName 캐시 이름
     * @param __key 캐시 키
     * @param __typeReference 타입 참조
     * @return 캐시된 데이터, 없으면 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String __cacheName, Object __key, com.fasterxml.jackson.core.type.TypeReference<T> __typeReference) {
        Object result = get(__cacheName, __key);
        if (result == null) {
            return null;
        }

        // LinkedHashMap인 경우 ObjectMapper로 변환
        if (result instanceof LinkedHashMap && objectMapper != null) {
            try {
                T converted = objectMapper.convertValue(result, __typeReference);
                log.debug("Successfully converted LinkedHashMap to complex type");
                return converted;
            } catch (Exception e) {
                log.warn("Failed to convert LinkedHashMap to complex type: {}", e.getMessage());
                return null;
            }
        }

        // ArrayList인 경우 ObjectMapper로 변환
        if (result instanceof ArrayList && objectMapper != null) {
            try {
                T converted = objectMapper.convertValue(result, __typeReference);
                log.debug("Successfully converted ArrayList to complex type");
                return converted;
            } catch (Exception e) {
                log.warn("Failed to convert ArrayList to complex type: {}", e.getMessage());
                return null;
            }
        }

        log.warn("Cannot convert {} to complex type", result.getClass().getSimpleName());
        return null;
    }

    /**
     * List 타입을 위한 편의 메서드
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String __cacheName, Object __key, Class<T> __elementType) {
        return get(__cacheName, __key, 
            new com.fasterxml.jackson.core.type.TypeReference<List<T>>(){});
    }

    /**
     * Map 타입을 위한 편의 메서드
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String __cacheName, Object __key, Class<K> __keyType, Class<V> __valueType) {
        return get(__cacheName, __key,
            new com.fasterxml.jackson.core.type.TypeReference<Map<K, V>>(){});
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
