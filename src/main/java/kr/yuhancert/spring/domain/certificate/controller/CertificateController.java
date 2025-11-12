package kr.yuhancert.spring.domain.certificate.controller;
import ch.qos.logback.classic.Logger;
import kr.yuhancert.spring.domain.certificate.dto.*;
import kr.yuhancert.spring.domain.certificate.service.CertificateService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/cert")
public class CertificateController {
    private final CertificateService certificateService;

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private Map<String, String> errorResponse = new HashMap<>();

    public CertificateController(CertificateService __certificateService) {
        this.certificateService = __certificateService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCertificate() {
        try {
            List<CertificateDTO> certificateDTO = certificateService.getCertificate();
            return ResponseEntity.ok(certificateDTO);
        }catch (Exception e) {
            logger.error("Error getting certificate list", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/tag")
    public ResponseEntity<?> getTag() {
        try {
            List<TagDTO> tagDTO = certificateService.getTagList();
            return ResponseEntity.ok(tagDTO);
        }catch (Exception e) {
            logger.error("Error getting tag list", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/organization")
    public ResponseEntity<?> getOrganization() {
        try {
            List<OrganizationDTO> organizationDTO = certificateService.getOrganizationList();
            return ResponseEntity.ok(organizationDTO);
        } catch (Exception e) {
            logger.error("Error getting organization list", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping(value = "/national")
    public ResponseEntity<?> getNationalSchedule() {
        try{
            List<NationalCertDateDTO> nationalDTO = certificateService.getNationalSchedule();
            return ResponseEntity.ok(nationalDTO);
        }catch (Exception e) {
            logger.error("Error getting national_cert_date list", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/data/{id}")
    public ResponseEntity<?> getCertData(@PathVariable("id") Long id) {
        try {
            CertDataDTO certDataDTO = certificateService.getCertData(id);
            return ResponseEntity.ok(certDataDTO);
        }catch (Exception e) {
            logger.error("Error getting certificate data", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getSchedule(@RequestParam("id") List<Long> ids) {
        try {
            List<ScheduleDTO> schedule = certificateService.getSchedule(ids);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            logger.error("Error getting schedule data", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/run-fallback")
    public ResponseEntity<String> runByFallback(@RequestParam String certName) throws Exception {
        certificateService.runFallback(certName);
        return ResponseEntity.ok("✅ fallback 자격증 실행 완료 (name: " + certName + ")");
    }

    // kr/yuhancert/spring/domain/certificate/controller/CertificateController.java

    // ✅ 1. 전체: 파이썬 실행 + JSON 저장 한꺼번에
    // ✅ 1. 전체: 파이썬 실행 + JSON 저장 한꺼번에
    //1번째: 내가 손 댄 곳 -> 파이썬 실행하고 json 저장까지 다 하는 친구
    @PostMapping("/run-public/{certId}")
    public ResponseEntity<String> runPublic(@PathVariable Long certId) throws Exception {
        certificateService.runPublicById(certId);
        return ResponseEntity.ok("✅ run_public 완료 (certId=" + certId + ")");
    }


}