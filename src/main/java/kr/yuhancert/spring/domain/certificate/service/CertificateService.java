package kr.yuhancert.spring.domain.certificate.service;

import kr.yuhancert.spring.domain.certificate.dto.*;
import kr.yuhancert.spring.domain.certificate.entity.*;
import kr.yuhancert.spring.domain.certificate.mapper.CertDataMapper;
import kr.yuhancert.spring.domain.certificate.mapper.CertificateMapper;
import kr.yuhancert.spring.domain.certificate.repository.*;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CertificateService {

    private final CacheService cacheService;
    Logger logger = LoggerFactory.getLogger(CertificateService.class);
    private final CertificateRepository certificateRepository;
    private final CertDataRepository certDataRepository;
    private final CertificateMapper certificateMapper;
    private final CertDataMapper certDataMapper;
    private List<Certificate> certificateEntities;
    private CertData certDataEntities;


    public CertificateService(CacheService __cacheService,
                              CertificateRepository __certificateRepository,
                              CertDataRepository __certDataRepository,
                              CertificateMapper __certificateMapper,
                              CertDataMapper __certDataMapper) {
        this.cacheService = __cacheService;
        this.certificateRepository = __certificateRepository;
        this.certDataRepository = __certDataRepository;
        this.certificateMapper = __certificateMapper;
        this.certDataMapper = __certDataMapper;
    }

    private void checkCertEntities() {

        if(certificateEntities == null || certificateEntities.isEmpty()) {
            certificateEntities = certificateRepository.findAll();
        }

//        if (certDataEntities == null || certDataEntities.isEmpty()) {
//            certDataEntities = certDataRepository.findAll().stream()
//                    .collect(Collectors.toMap(CertData::getId, Function.identity()));
//        }

    }

    public List<CertificateDTO> getCertificate() {
        String CACHE_KEY_CERT = "cert";
        List<CertificateDTO> cacheCertificate = cacheService.getList(CacheList.CERT_CACHE.getName(), CACHE_KEY_CERT, CertificateDTO.class);
        if (cacheCertificate != null) {
            return cacheCertificate;
        }

        checkCertEntities();

        List<CertificateDTO> certificateDTO = certificateMapper.toCertificateDTOList(certificateEntities);

        cacheService.put(CacheList.CERT_CACHE.getName(), CACHE_KEY_CERT, certificateDTO);

        return certificateDTO;
    }

    public CertDataDTO getCertData(Long __id) {
        CertDataDTO cacheCertDataDTO = cacheService.get(CacheList.CERT_DATA_CACHE.getName(), __id, CertDataDTO.class);
        if (cacheCertDataDTO != null) {
            return cacheCertDataDTO;
        }

        certDataEntities = certDataRepository.findById(__id).orElse(null);

        CertDataDTO certDataDTO = certDataMapper.toCertDataDTO(certDataEntities);

        cacheService.put(CacheList.CERT_DATA_CACHE.getName(), __id, certDataDTO);

        return certDataDTO;
    }

}