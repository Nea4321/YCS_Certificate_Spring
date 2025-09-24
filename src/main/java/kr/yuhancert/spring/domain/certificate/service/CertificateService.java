package kr.yuhancert.spring.domain.certificate.service;

import kr.yuhancert.spring.domain.certificate.dto.*;
import kr.yuhancert.spring.domain.certificate.entity.*;
import kr.yuhancert.spring.domain.certificate.mapper.CertDataMapper;
import kr.yuhancert.spring.domain.certificate.mapper.CertificateMapper;
import kr.yuhancert.spring.domain.certificate.repository.*;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import kr.yuhancert.spring.infra.config.CertConfig;
import kr.yuhancert.spring.infra.config.CertConfigRegistry;
import kr.yuhancert.spring.infra.crawling.engine.EngineRunner;
import kr.yuhancert.spring.infra.crawling.manager.CertificateExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final CertConfigRegistry configRegistry;
    private final EngineRunner engineRunner;
    private final JsonCertificateParser parser;
    private final CertificateExecutor executor;

    @Value("${public.script:../Engine/public_cert_api/run_public.py}")
    private String publicScript;

    @Value("${public.root:${user.dir}}")   // 기본값: 프로젝트 루트
    private String publicRoot;

    @Value("${cert.json.root:src/main/resources/json}")
    private String jsonRoot;


    public CertificateService(
            CacheService cacheService,
            CertificateRepository certificateRepository,
            CertDataRepository certDataRepository,

            CertificateMapper certificateMapper,
            CertDataMapper certDataMapper,
            CertConfigRegistry certConfigRegistry,
            EngineRunner engineRunner,
            JsonCertificateParser parser,
            CertificateExecutor executor
    ) {
        this.cacheService = cacheService;
        this.certificateRepository = certificateRepository;
        this.certDataRepository = certDataRepository;
        this.certificateMapper = certificateMapper;
        this.certDataMapper = certDataMapper;
        this.configRegistry = certConfigRegistry;
        this.engineRunner = engineRunner;
        this.parser = parser;
        this.executor = executor;
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

    public void runFallback(String certName) throws Exception {
        executor.runAndSave(null, certName);
    }

    // CertificateService.java
    public void runPublicById(Long certId) throws Exception {
        // 1) 자격증 조회 + jmcd
        Certificate cert = certificateRepository.findById(certId)
                .orElseThrow(() -> new IllegalArgumentException("no certificate: " + certId));

        String jmcd = cert.getJmcd().getJmcd();
        if (jmcd == null || jmcd.isBlank()) {
            throw new IllegalStateException("jmcd 가 비어있습니다. id=" + certId);
        }

        // 2) 출력 디렉터리 (예: .../src/main/resources/json)
        Path outDir = Paths.get(System.getProperty("user.dir")).resolve(jsonRoot);
        Files.createDirectories(outDir);

        // 3) run_public.py 실행 인자 구성 (가변 리스트)
        List<String> extraArgs = new ArrayList<>();
        Collections.addAll(extraArgs,
                "--jmcd", jmcd,
                "--root", publicRoot
                // 필요 시 옵션 추가: "--mode","snapshot","--steps","parse,normalize","--force"
        );
        // 표시명 패치 옵션을 run 전에 추가
        extraArgs.addAll(Arrays.asList("--display-name", cert.getCertificateName()));

        // 실행 (certName은 사용하지 않으므로 null)
        engineRunner.run(publicScript, null, null, outDir.toString(), extraArgs);

        // 4) 정규화 결과 파일 경로 결정
        Path norm1 = outDir.resolve(jmcd + ".norm.json");               // out/<jmcd>.norm.json
        Path norm2 = outDir.resolve(jmcd).resolve(jmcd + ".norm.json"); // out/<jmcd>/<jmcd>.norm.json
        Path finalJson = Files.exists(norm1) ? norm1 : (Files.exists(norm2) ? norm2 : null);
        if (finalJson == null) {
            throw new IllegalStateException("정규화 결과를 찾지 못했습니다: " + norm1 + " 또는 " + norm2);
        }

        // 5) JSON → DB 저장
        parser.parseAndSave(finalJson.toString(), certId);
    }



}