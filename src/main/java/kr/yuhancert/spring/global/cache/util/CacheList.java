package kr.yuhancert.spring.global.cache.util;

import kr.yuhancert.spring.global.cache.model.CertCache;

import java.util.List;

public class CacheList {

    private CacheList() {} /// 유틸리티 클래스로 쓰기위해 인스턴스화 방지

    public static final CertCache FACULTY_CACHE = CertCache.of("facultyCache", 1, 12, 50);
    public static final CertCache DEPARTMENT_CACHE = CertCache.of("departmentCache", 1, 12, 200);
    public static final CertCache MAJOR_CACHE = CertCache.of("majorCache", 1, 12, 300);
    public static final CertCache DEPT_MAP_CACHE = CertCache.of("deptMapCache", 1, 12, 300);
    public static final CertCache DEPT_LIST_CACHE = CertCache.of("deptListCache", 1, 24, 100);
    public static final CertCache DEPT_DATA_CACHE = CertCache.of("deptDataCache", 1, 24, 100);

    public static final CertCache CERT_CACHE = CertCache.of("certCache", 1, 24, 300);
    public static final CertCache CERT_DEPT_CACHE = CertCache.of("certCache", 1, 24, 300);
    public static final CertCache CERT_DATA_CACHE = CertCache.of("certDataCache", 1, 12, 300);
    public static final CertCache TAG_CACHE = CertCache.of("tagCache", 1, 24, 100);
    public static final CertCache ORG_CACHE = CertCache.of("orgCache", 1, 24, 100);


    /// 모든 캐시 목록
    public static final List<CertCache> ALL_CACHES = List.of(
            /// 학사 관련
            FACULTY_CACHE, DEPARTMENT_CACHE, MAJOR_CACHE, DEPT_MAP_CACHE, DEPT_LIST_CACHE, DEPT_DATA_CACHE,
            /// 자격증 관련
            CERT_CACHE, CERT_DEPT_CACHE, CERT_DATA_CACHE, TAG_CACHE, ORG_CACHE

    );

    /// 도메인별 캐시 그룹
    public static final List<CertCache> DEPT_CACHES = List.of(
        FACULTY_CACHE, DEPARTMENT_CACHE, MAJOR_CACHE, DEPT_MAP_CACHE, DEPT_LIST_CACHE, DEPT_DATA_CACHE
    );

    public static final List<CertCache> CERT_CACHES = List.of(
        CERT_CACHE, CERT_DEPT_CACHE, CERT_DATA_CACHE, TAG_CACHE, ORG_CACHE
    );


}