package kr.yuhancert.spring.domain.certificate.controller;
import ch.qos.logback.classic.Logger;
import kr.yuhancert.spring.domain.certificate.dto.*;
import kr.yuhancert.spring.domain.certificate.service.CertificateService;
import kr.yuhancert.spring.domain.certificate.service.JsonCertificateParser;
import kr.yuhancert.spring.infra.crawling.engine.EngineRunner;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ✅ 1. 전체: 파이썬 실행 + JSON 저장 한꺼번에
    //1번째: 내가 손 댄 곳 -> 파이썬 실행하고 json 저장까지 다 하는 것
    @PostMapping("/run/{certId}")
    public ResponseEntity<String> runById(@PathVariable Long certId) throws Exception {
        certificateService.runCertificate(certId);
        return ResponseEntity.ok("✅ 자격증 실행 완료 (certId: " + certId + ")");
    }

    @PostMapping("/run-fallback")
    public ResponseEntity<String> runByFallback(@RequestParam String certName) throws Exception {
        certificateService.runFallback(certName);
        return ResponseEntity.ok("✅ fallback 자격증 실행 완료 (name: " + certName + ")");
    }
}