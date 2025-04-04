package kr.yuhancert.spring.global.cache.util;

import kr.yuhancert.spring.global.cache.model.CertCache;

import java.util.List;

public class CacheList {

    private CacheList() {} /// 유틸리티 클래스로 쓰기위해 인스턴스화 방지

    public static final CertCache DEPT_LIST_CACHE = CertCache.of("deptListCache", 1, 24, 100);
    public static final CertCache FACULTY_CACHE = CertCache.of("facultyCache", 1, 12, 50);
    public static final CertCache DEPARTMENT_CACHE = CertCache.of("departmentCache", 1, 12, 200);
    public static final CertCache MAJOR_CACHE = CertCache.of("majorCache", 1, 12, 300);
    public static final CertCache DEPT_MAP_CACHE = CertCache.of("deptMapCache", 1, 12, 300);


    /// 모든 캐시 목록
    public static final List<CertCache> ALL_CACHES = List.of(
            /// 학사 관련
            DEPT_LIST_CACHE, FACULTY_CACHE, DEPARTMENT_CACHE, MAJOR_CACHE, DEPT_MAP_CACHE

    );

    /// 도메인별 캐시 그룹
    public static final List<CertCache> DEPT_CACHES = List.of(
            DEPT_LIST_CACHE, FACULTY_CACHE, DEPARTMENT_CACHE, MAJOR_CACHE, DEPT_MAP_CACHE
    );


}
