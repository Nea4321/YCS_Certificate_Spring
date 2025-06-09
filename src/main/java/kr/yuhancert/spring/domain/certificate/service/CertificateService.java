package kr.yuhancert.spring.domain.certificate.service;

import kr.yuhancert.spring.domain.certificate.dto.CertDeptDto;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import kr.yuhancert.spring.domain.certificate.entity.CertDept;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.repository.*;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CacheService cacheService;
    Logger logger = LoggerFactory.getLogger(CertificateService.class);
    private final CertificateRepository certificateRepository;
    private final CertDeptRepository certDeptRepository;
    private final CertDataRepository certDataRepository;
    private List<Certificate> certificateEntities;
    private List<CertDept> certDeptEntities;
    private Map<Long, CertData> certDataEntities;


    public CertificateService(CacheService __cacheService,
                              CertificateRepository __certificateRepository,
                              CertDeptRepository __certDeptRepository,
                              CertDataRepository __certDataRepository) {
        this.cacheService = __cacheService;
        this.certificateRepository = __certificateRepository;
        this.certDeptRepository = __certDeptRepository;
        this.certDataRepository = __certDataRepository;
    }

    private void checkCertEntities() {

        if(certificateEntities == null || certificateEntities.isEmpty()) {
            certificateEntities = certificateRepository.findAll();
        }

        if (certDeptEntities == null || certDeptEntities.isEmpty()) {
            certDeptEntities = certDeptRepository.findAll();
        }

        if (certDataEntities == null || certDataEntities.isEmpty()) {
            certDataEntities = certDataRepository.findAll().stream()
                    .collect(Collectors.toMap(CertData::getId, Function.identity()));
        }

    }

    public List<Certificate> getCertificate() {
        String CACHE_KEY_CERT = "cert";
        List<Certificate> cacheCertificate = cacheService.get(CacheList.CERT_CACHE.getName(), CACHE_KEY_CERT);
        if (cacheCertificate != null) {
            return cacheCertificate;
        }

        checkCertEntities();

        cacheService.put(CacheList.CERT_CACHE.getName(), CACHE_KEY_CERT, this.certificateEntities);

        return this.certificateEntities;
    }

    public List<CertDeptDto> getCertDept() {
        String CACHE_KEY_CD = "cert_dept";
        List<CertDeptDto> cacheCertDept = cacheService.get(CacheList.CERT_DEPT_CACHE.getName(), CACHE_KEY_CD);
        if (cacheCertDept != null) {
            return cacheCertDept;
        }

        checkCertEntities();

        List<CertDeptDto> certDeptDto = certDeptEntities.stream()
                .map(cd -> new CertDeptDto(cd.getId(), cd.getCertificate().getId(), cd.getDeptMap().getId()))
                .toList();

        cacheService.put(CacheList.CERT_DEPT_CACHE.getName(), CACHE_KEY_CD, certDeptDto);

        return certDeptDto;
    }

    public CertData getCertData(Long __id) {
        CertData cacheCertData = cacheService.get(CacheList.CERT_DATA_CACHE.getName(), __id);
        if (cacheCertData != null) {
            return cacheCertData;
        }

        checkCertEntities();

        cacheService.put(CacheList.CERT_DATA_CACHE.getName(), __id, this.certDataEntities.get(__id));

        return this.certDataEntities.get(__id);
    }

}