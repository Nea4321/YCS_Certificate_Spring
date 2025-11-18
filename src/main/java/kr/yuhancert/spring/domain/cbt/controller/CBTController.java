package kr.yuhancert.spring.domain.cbt.controller;

import ch.qos.logback.classic.Logger;
import kr.yuhancert.spring.domain.cbt.dto.CBTDTO;
import kr.yuhancert.spring.domain.cbt.dto.PreviousDTO;
import kr.yuhancert.spring.domain.cbt.service.CBTService;
import kr.yuhancert.spring.domain.certificate.dto.CertificateDTO;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/cbt")
public class CBTController {

    private final CBTService cbtService;
    private Map<String, String> errorResponse = new HashMap<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public CBTController(CBTService __cbtService) {
        this.cbtService = __cbtService;
    }

    @GetMapping
    public ResponseEntity<?> getCBTList() {
        try {
            List<CertificateDTO> certificateDTOList = cbtService.getCBTList();
            logger.info(certificateDTOList.toString());
            return ResponseEntity.ok(certificateDTOList);
        } catch (Exception e) {
            logger.error("Error getting certificate list", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(errorResponse);
        }
    }

    @GetMapping(params = "cert_id")
    public ResponseEntity<?> getQuestionInfo(@RequestParam("cert_id") Long __certId) {
        try {
            List<CBTDTO> cbtDTOList = cbtService.getCBTDTOList(__certId);
            logger.info(cbtDTOList.toString());
            return ResponseEntity.ok(cbtDTOList);
        } catch (Exception e) {
            logger.error("Error getting certificate info", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping(params = "question_info_id")
    public ResponseEntity<?> getPrevious(@RequestParam("question_info_id") Long __questionInfoId) {
        try {
            PreviousDTO previousDTO = cbtService.getPrevious(__questionInfoId);
            logger.info(previousDTO.toString());
            return ResponseEntity.ok(previousDTO);
        } catch (Exception e) {
            logger.error("Error getting previous question", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
