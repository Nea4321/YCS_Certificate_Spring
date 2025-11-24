package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.entity.User;
import kr.yuhancert.spring.domain.auth.repository.UserRepository;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.cbt.entity.Answer;
import kr.yuhancert.spring.domain.cbt.entity.Previous;
import kr.yuhancert.spring.domain.cbt.entity.Question;
import kr.yuhancert.spring.domain.cbt.mapper.PreviousMapper;
import kr.yuhancert.spring.domain.cbt.repository.AnswerRepository;
import kr.yuhancert.spring.domain.cbt.repository.PreivousRepository;
import kr.yuhancert.spring.domain.cbt.repository.QuestionRepository;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.repository.CertificateRepository;
import kr.yuhancert.spring.domain.user.dto.*;
import kr.yuhancert.spring.domain.user.entity.UserAnswer;
import kr.yuhancert.spring.domain.user.entity.UserCbtHistory;
import kr.yuhancert.spring.domain.user.mapper.UserAnswerMapper;
import kr.yuhancert.spring.domain.user.mapper.UserCbtHistoryCertMapper;
import kr.yuhancert.spring.domain.user.repository.UserAnswerRepository;
import kr.yuhancert.spring.domain.user.repository.UserCbtHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserCbtHistoryService {
    private final JwtService jwtService;
    private final UserCbtHistoryRepository userCbtHistoryRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final PreivousRepository preivousRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserCbtHistoryCertMapper userCbtHistoryCertMapper;
    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper userAnswerMapper;
    private final PreviousMapper previousMapper;

    public UserCbtHistoryService(JwtService __jwtService, UserCbtHistoryRepository __userCbtHistoryRepository, CertificateRepository certificateRepository
    , UserRepository __userRepository, PreivousRepository __previousRepository, QuestionRepository __questionRepository, AnswerRepository __answerRepository, UserCbtHistoryCertMapper __userCbtHistoryCertMapper, UserAnswerRepository userAnswerRepository, UserAnswerMapper userAnswerMapper, PreviousMapper previousMapper) {
        this.jwtService = __jwtService;
        this.userCbtHistoryRepository = __userCbtHistoryRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = __userRepository;
        this.preivousRepository = __previousRepository;
        this.questionRepository = __questionRepository;
        this.answerRepository = __answerRepository;
        this.userCbtHistoryCertMapper = __userCbtHistoryCertMapper;
        this.userAnswerRepository = userAnswerRepository;
        this.userAnswerMapper = userAnswerMapper;
        this.previousMapper = previousMapper;
    }

    // cbt 기록 정보 요청
    public List<UserCbtHistoryCertDTO> getCbtHistory(HttpServletRequest request) {
        try {

            Claims claims = jwtService.parseClaims(request);
            Object idObj = claims.get("id");
            Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

            // 사용자 CBT 기록 조회
            List<UserCbtHistory> histories = userCbtHistoryRepository.findAllByUser_Id(userId);

            // 기록 없으면 빈 리스트 반환
            if (histories.isEmpty()) {
                log.info("CBT 기록 없음: userId={}", userId);
                return Collections.emptyList();
            }

            Map<Long, List<UserCbtHistory>> userCbtHistoryMap = histories.stream()
                    .collect(Collectors.groupingBy(
                            h -> h.getCertificate().getId()
                    ));
/*
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
 */
            return userCbtHistoryCertMapper.toUserCbtHistoryCertDTOList(userCbtHistoryMap);
        } catch (Exception e) {
            log.error("CBT 기록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("CBT 기록 조회 중 오류 발생");
        }
    }

    public UserPreviousDTO getUserPreviousDTO(Long __previousId, HttpServletRequest request) {
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        Previous previous = preivousRepository.findById(__previousId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다."));

        if (!Objects.equals(previous.getTypeId(), userId))
            throw new IllegalArgumentException("유저 정보가 다릅니다");

        List<UserAnswer> userAnswerList = userAnswerRepository.findByPrevious(previous);

        return new UserPreviousDTO(
                previousMapper.toPreviousDTO(previous),
                userAnswerMapper.toUserAnswerDTOList(userAnswerList)
        );
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

            Certificate cert = certificateRepository.findById(dto.getCertificate_id())
                    .orElseThrow(() -> new RuntimeException("해당 certificate 을 찾을 수 없습니다."));

            Previous previous = preivousRepository.findById(dto.getPrevious_id())
                    .orElseThrow(() -> new RuntimeException("해당 기록을 찾을 수 없습니다."));

            UserCbtHistory history = new UserCbtHistory();
            history.setUser(user);
            history.setCertificate(cert);
            history.setScore(dto.getScore());
            history.setCorrectCount(dto.getCorrect_Count());
            history.setPrevious(previous);
            history.setLeftTime(dto.getLeft_time());

            userCbtHistoryRepository.save(history);

            log.info("CBT 기록 저장 완료: userId={}, certId={}", userId, dto.getCertificate_id());

            List<UserAnswerDTO> answers = dto.getAnswers();

            List<UserAnswer> userAnswerList = new ArrayList<>();

            List<Question> questions = questionRepository.findByCertificate(cert);

            List<Answer> answerList = answerRepository.findByQuestionIn(questions);

            Map<Long, Answer> answerMap = answerList.stream()
                    .collect(Collectors.toMap(Answer::getId, a -> a));

            for (UserAnswerDTO a : answers) {

                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setUser(user);
                userAnswer.setCertificate(cert);
                userAnswer.setAnswer(answerMap.get(a.getAnswer_id()));
                userAnswer.setBool(a.getBool());
                userAnswer.setPrevious(previous);

                userAnswerList.add(userAnswer);
            }

            userAnswerRepository.saveAll(userAnswerList);

            log.info("유저 답안 기록 저장 완료: userId={}, certId={}", userId, dto.getCertificate_id());

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
