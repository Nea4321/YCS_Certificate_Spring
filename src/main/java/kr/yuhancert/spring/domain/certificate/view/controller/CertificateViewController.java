package kr.yuhancert.spring.domain.certificate.view.controller;

import jakarta.persistence.EntityNotFoundException;
import kr.yuhancert.spring.domain.certificate.view.dto.*;
import kr.yuhancert.spring.domain.certificate.view.service.CertificateViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Slf4j
public class CertificateViewController {

    private final CertificateViewService service;

    private <T> ResponseEntity<T> noStore(T body) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(body);
    }

    // ---- 일정 (항상 events가 빈 배열 보장) ----
    @GetMapping("/{id}/schedule")
    public ResponseEntity<ScheduleDto> schedule(@PathVariable Long id) {
        try {
            ScheduleDto dto = service.getSchedule(id);
            return noStore(dto != null ? dto : ScheduleDto.empty());
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            return noStore(ScheduleDto.empty());
        } catch (Exception e) {
            log.error("schedule error id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .build();
        }
    }

    @GetMapping("/{id}/exam-info")
    public ResponseEntity<ExamInfoDto> examInfo(@PathVariable Long id) {
        try {
            ExamInfoDto dto = service.getExamInfo(id);
            return noStore(dto != null ? dto : ExamInfoDto.empty());
        } catch (Exception e) {
            log.error("exam-info error id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .build();
        }
    }

    @GetMapping("/{id}/basic-info")
    public ResponseEntity<BasicInfoDto> basicInfo(@PathVariable Long id) {
        try {
            BasicInfoDto dto = service.getBasicInfo(id);
            return noStore(dto != null ? dto : BasicInfoDto.empty());
        } catch (Exception e) {
            log.error("basic-info error id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .build();
        }
    }

    @GetMapping("/{id}/preference")
    public ResponseEntity<PreferenceDto> preference(@PathVariable Long id) {
        try {
            PreferenceDto dto = service.getPreference(id);
            return noStore(dto != null ? dto : PreferenceDto.empty());
        } catch (Exception e) {
            log.error("preference error id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .build();
        }
    }

    // ---- 통합 뷰 ----
    @GetMapping("/{id}/public")
    public ResponseEntity<CertificatePublicViewDto> publicView(@PathVariable Long id) {
        try {
            CertificatePublicViewDto dto = service.getPublic(id);
            return noStore(dto != null ? dto : CertificatePublicViewDto.empty());
        } catch (Exception e) {
            log.error("public-view error id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .cacheControl(CacheControl.noStore())
                    .build();
        }
    }
}
