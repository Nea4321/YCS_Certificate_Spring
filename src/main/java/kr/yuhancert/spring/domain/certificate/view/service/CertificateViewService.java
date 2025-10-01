package kr.yuhancert.spring.domain.certificate.view.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import kr.yuhancert.spring.domain.certificate.repository.CertDataRepository;
import kr.yuhancert.spring.domain.certificate.view.dto.*;
import kr.yuhancert.spring.domain.certificate.view.mapper.PublicNormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateViewService {

    private final CertDataRepository certDataRepository;
    private final ObjectMapper objectMapper;
    private final PublicNormMapper mapper;

    /** certId 기준으로 contents JSON을 파싱해서 루트 노드 반환 */
    private JsonNode loadRoot(Long certId) {
        CertData cd = certDataRepository.findById(certId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "cert_data not found: certId=" + certId));
        try {
            return objectMapper.readTree(cd.getContents());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid JSON contents for certId=" + certId, e);
        }
    }

    /** ✅ 일정: cert_data 기반 파싱, 실패해도 항상 빈 events 반환 */
    public ScheduleDto getSchedule(Long certId) {
        try {
            JsonNode root = loadRoot(certId);
            ScheduleDto dto = mapper.toSchedule(root);   // <-- 아래 mapper 추가
            return dto != null ? dto : ScheduleDto.empty();
        } catch (Exception e) {
            log.error("getSchedule failed id={}", certId, e);
            return ScheduleDto.empty();
        }
    }

    public ExamInfoDto getExamInfo(Long certId) {
        return mapper.toExamInfo(loadRoot(certId));
    }
    public BasicInfoDto getBasicInfo(Long certId) {
        return mapper.toBasicInfo(loadRoot(certId));
    }
    public PreferenceDto getPreference(Long certId) {
        return mapper.toPreference(loadRoot(certId));
    }

    /** 통합: 메타 포함 한 번에 */
    public CertificatePublicViewDto getPublic(Long certId) {
        return mapper.toPublic(loadRoot(certId));
    }
}
