package kr.yuhancert.spring.global.cache.model;

import lombok.Getter;

import java.time.Duration;

@Getter
public class CertCache {
    private final String name;          // 캐시 이름
    private final Duration localTtl;    // Caffeine TTL
    private final Duration remoteTtl;   // Redis TTL
    private final int maxSize;          // Caffeine 최대 크기

    private CertCache(String __name, Duration __localTtl, Duration __remoteTtl, int __maxSize) {
        this.name = __name;
        this.localTtl = __localTtl;
        this.remoteTtl = __remoteTtl;
        this.maxSize = __maxSize;
    }

    public static CertCache of(String __name, long __localHours, long __remoteHours, int __maxSize) {
        return new CertCache(
                __name,
                Duration.ofHours(__localHours),
                Duration.ofHours(__remoteHours),
                __maxSize
        );
    }

    /*
    public static CertCache ofMinutes(String __name, long __localHours, long __remoteHours, int __maxSize) {
        return new CertCache(
                __name,
                Duration.ofHours(__localHours),
                Duration.ofHours(__remoteHours),
                __maxSize
        );
    }
    */

}
