package kr.yuhancert.spring.domain.certificate.controller;

import ch.qos.logback.classic.Logger;
import kr.yuhancert.spring.domain.certificate.dto.*;
import kr.yuhancert.spring.domain.certificate.entity.*;
import kr.yuhancert.spring.domain.certificate.service.CertificateService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public CertificateController(CertificateService __certificateService) { this.certificateService = __certificateService; }

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

    @GetMapping("/dept")
    public ResponseEntity<?> getCertDept() {
        try {
            List<CertDeptDTO> certDept = certificateService.getCertDept();
            return ResponseEntity.ok(certDept);
        }catch (Exception e) {
            logger.error("Error getting certificate department mapping", e);
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

}