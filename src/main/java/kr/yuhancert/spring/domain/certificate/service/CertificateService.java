package kr.yuhancert.spring.domain.certificate.service;

import kr.yuhancert.spring.domain.certificate.dto.*;
import kr.yuhancert.spring.domain.certificate.entity.*;
import kr.yuhancert.spring.domain.certificate.mapper.*;
import kr.yuhancert.spring.domain.certificate.repository.*;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CacheService cacheService;
    Logger logger = LoggerFactory.getLogger(CertificateService.class);
    private final CertificateRepository certificateRepository;
    private final CertDataRepository certDataRepository;
    private final TagRepository tagRepository;
    private final TagMapRepository tagMapRepository;
    private final OrganizationRepository organizationRepository;
    private final NationalCertDateRepository nationalCertDateRepository;

    private final CertificateMapper certificateMapper;
    private final CertDataMapper certDataMapper;
    private final TagMapper tagMapper;
    private final OrganizationMapper organizationMapper;
    private List<Certificate> certificateEntities;
    private List<Tag>  tagEntities;
    private List<TagMap> tagMapEntities;
    private List<Organization> organizationEntities;
    private CertData certDataEntities;
    private final CertConfigRegistry configRegistry;
    private final EngineRunner engineRunner;
    private final JsonCertificateParser parser;
    private final CertificateExecutor executor;
    private final NationalCertDateMapper nationalCertDateMapper;

    @Value("${public.script:../Engine/public_cert_api/run_public.py}")
    private String publicScript;

    @Value("${public.root:${user.dir}}")   // 기본값: 프로젝트 루트
    private String publicRoot;

    @Value("${cert.json.root:src/main/resources/json}")
    private String jsonRoot;


    public CertificateService(
            CacheService __cacheService,
            CertificateRepository __certificateRepository,
            CertDataRepository __certDataRepository,
            TagRepository __tagRepository,
            TagMapRepository __tagMapRepository,
            OrganizationRepository organizationRepository,

            OrganizationMapper organizationMapper,
            CertificateMapper __certificateMapper,
            CertDataMapper __certDataMapper,
            TagMapper __tagMapper,
            CertConfigRegistry certConfigRegistry,
            EngineRunner engineRunner,
            JsonCertificateParser parser,
            CertificateExecutor executor,
            NationalCertDateRepository nationalCertDateRepository,
            NationalCertDateMapper nationalCertDateMapper
    ) {
        this.cacheService = __cacheService;
        this.certificateRepository = __certificateRepository;
        this.certDataRepository = __certDataRepository;
        this.tagRepository = __tagRepository;
        this.tagMapRepository = __tagMapRepository;
        this.organizationRepository = organizationRepository;
        this.certificateMapper = __certificateMapper;
        this.certDataMapper = __certDataMapper;
        this.tagMapper = __tagMapper;
        this.organizationMapper = organizationMapper;
        this.configRegistry = certConfigRegistry;
        this.engineRunner = engineRunner;
        this.parser = parser;
        this.executor = executor;
        this.nationalCertDateRepository = nationalCertDateRepository;
        this.nationalCertDateMapper = nationalCertDateMapper;
    }

//    private void checkCertEntities() {
//
//        if(certificateEntities == null || certificateEntities.isEmpty()) {
//            certificateEntities = certificateRepository.findAll();
//        }
//
//        if(tagEntities == null || tagEntities.isEmpty()) {
//            tagEntities = tagRepository.findAll();
//        }
//
//        if(tagMapEntities == null || tagMapEntities.isEmpty()) {
//            tagMapEntities = tagMapRepository.findAll();
//        }
//
////        if (certDataEntities == null || certDataEntities.isEmpty()) {
////            certDataEntities = certDataRepository.findAll().stream()
////                    .collect(Collectors.toMap(CertData::getId, Function.identity()));
////        }
//
//    }

    public List<CertificateDTO> getCertificate() {
        String CACHE_KEY_CERT = "cert";
        List<CertificateDTO> cacheCertificate = cacheService.getList(CacheList.CERT_CACHE.getName(), CACHE_KEY_CERT, CertificateDTO.class);
        if (cacheCertificate != null) {
            return cacheCertificate;
        }

        if(certificateEntities == null || certificateEntities.isEmpty()) {
            certificateEntities = certificateRepository.findAll();
        }

        if(tagMapEntities == null || tagMapEntities.isEmpty()) {
            tagMapEntities = tagMapRepository.findAll();
        }

        Map<Long, List<Long>> tagMapMap = tagMapEntities.stream()
                .collect(Collectors.groupingBy(
                tm -> tm.getCertificate().getId(),
                Collectors.mapping(tm -> tm.getTag().getId(), Collectors.toList())
        ));

        List<CertificateDTO> certificateDTO = certificateMapper.toCertificateDTOList(certificateEntities,  tagMapMap);

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

    public List<TagDTO> getTagList() {
        List<TagDTO> cacheTagList = cacheService.getList(CacheList.TAG_CACHE.getName(), CacheList.TAG_CACHE, TagDTO.class);
        if (cacheTagList != null) {
            return cacheTagList;
        }

        if(tagEntities == null || tagEntities.isEmpty()) {
            tagEntities = tagRepository.findAll();
        }

        List<TagDTO> tagDTOList = tagMapper.toTagListDTO(tagEntities);

        cacheService.put(CacheList.TAG_CACHE.getName(), CacheList.TAG_CACHE, tagDTOList);

        return tagDTOList;
    }

    public List<NationalCertDateDTO> getNationalSchedule() {
        List<NationalCertDate> allEntities = nationalCertDateRepository.findAll();
        return nationalCertDateMapper.toScheduleDTOList(allEntities);
    }

    public List<OrganizationDTO> getOrganizationList() {
        List<OrganizationDTO> cacheOrganization = cacheService.getList(CacheList.ORG_CACHE.getName(), CacheList.ORG_CACHE, OrganizationDTO.class);
        if (cacheOrganization != null) {
            return cacheOrganization;
        }

        if(organizationEntities == null || organizationEntities.isEmpty()) {
            organizationEntities = organizationRepository.findAll();
        }

        List<OrganizationDTO> orgDTOList = organizationMapper.toOrganizationDTOList(organizationEntities);

        cacheService.put(CacheList.ORG_CACHE.getName(), CacheList.ORG_CACHE, orgDTOList);

        return orgDTOList;
    }

    public List<ScheduleDTO> getSchedule(List<Long> __ids) {
        //캐시 넣으면 더 복잡해질거 같아서 그냥 넘기기
        return certDataRepository.findSchedulesByIds(__ids);

    }

    public void runFallback(String certName) throws Exception {
        executor.runAndSave(null, certName);
    }

    // CertificateService.java
    public void runPublicById(Long certId) throws Exception {
        // 1) 자격증 조회 + jmcd
        Certificate cert = certificateRepository.findById(certId)
                .orElseThrow(() -> new IllegalArgumentException("no certificate: " + certId));

        String jmcd = cert.getJmcd();
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