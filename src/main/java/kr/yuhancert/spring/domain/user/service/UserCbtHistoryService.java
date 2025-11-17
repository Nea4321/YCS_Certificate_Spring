package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.entity.User;
import kr.yuhancert.spring.domain.auth.repository.UserRepository;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.repository.CertificateRepository;
import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryDTO;
import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryResponseDTO;
import kr.yuhancert.spring.domain.user.entity.UserCbtHistory;
import kr.yuhancert.spring.domain.user.mapper.UserCbtHistoryMapper;
import kr.yuhancert.spring.domain.user.repository.UserCbtHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserCbtHistoryService {
    private final JwtService jwtService;
    private final UserCbtHistoryRepository userCbtHistoryRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final UserCbtHistoryMapper userCbtHistoryMapper;

    public UserCbtHistoryService(JwtService __jwtService, UserCbtHistoryRepository __userCbtHistoryRepository, CertificateRepository certificateRepository
    , UserRepository __userRepository, UserCbtHistoryMapper __userCbtHistoryMapper) {
        this.jwtService = __jwtService;
        this.userCbtHistoryRepository = __userCbtHistoryRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = __userRepository;
        this.userCbtHistoryMapper = __userCbtHistoryMapper;
    }

    // cbt 기록 정보 요청
    public List<UserCbtHistoryResponseDTO> getCbtHistory(HttpServletRequest request) {
        try {

            Claims claims = jwtService.parseClaims(request);
            Object idObj = claims.get("id");
            Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

            // 사용자 CBT 기록 조회
            List<UserCbtHistory> histories = userCbtHistoryRepository.findDistinctCertificateByUser_Id(userId);

            // 기록 없으면 빈 리스트 반환
            if (histories.isEmpty()) {
                log.info("CBT 기록 없음: userId={}", userId);
                return Collections.emptyList();
            }

            // DTO 변환
            return histories.stream().map(history -> {

                Certificate cert = certificateRepository
                        .findById(history.getCertificate().getId())
                        .orElse(null);

                if (cert == null) {
                    log.warn("certificate 정보 없음: certificateId={} (userId={})",
                            history.getCertificate().getId(), userId);
                }

                String certName = (cert != null) ? cert.getCertificateName() : null;

                return new UserCbtHistoryResponseDTO(
                        history.getCertificate().getId(),
                        certName,
                        history.getScore(),
                        history.getCorrectCount(),
                        history.getPrevious().getId(),
                        history.getCreatedAt(),
                        history.getLeftTime()
                );
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("CBT 기록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("CBT 기록 조회 중 오류 발생");
        }
    }

    public ResponseEntity<?> addCbtHistory(UserCbtHistoryDTO dto, HttpServletRequest request) {

        try {
            Claims claims = jwtService.parseClaims(request);
            Object idObj = claims.get("id");
            Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;
            log.info("CBT 기록 저장 완료: "+dto.getLeft_time());
            List<UserCbtHistory> histories = userCbtHistoryRepository.findAllByUser_IdOrderByCreatedAtAsc(userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));

            certificateRepository.findById(dto.getCertificate_id())
                    .orElseThrow(() -> new RuntimeException("해당 certificate 을 찾을 수 없습니다."));

            UserCbtHistory history = new UserCbtHistory();
            history.setUser(user);
            history.setCertificateId(dto.getCertificate_id());
            history.setLefttime(dto.getLeft_time());
            history.setScore(dto.getScore());
            history.setCorrectCount(dto.getCorrect_Count());

            userCbtHistoryRepository.save(history);

            log.info("CBT 기록 저장 완료: userId={}, certId={}", userId, dto.getCertificate_id());

            return ResponseEntity.ok("CBT 기록 저장 성공");

        } catch (RuntimeException e) {

            log.warn("CBT 기록 저장 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            // 예상 못한 예외
            log.error("CBT 기록 저장 중 서버 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("CBT 기록 저장 중 오류 발생");
        }
    }

}
