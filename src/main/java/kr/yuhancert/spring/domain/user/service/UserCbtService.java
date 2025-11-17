package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.cbt.entity.Answer;
import kr.yuhancert.spring.domain.cbt.entity.Question;
import kr.yuhancert.spring.domain.cbt.repository.AnswerRepository;
import kr.yuhancert.spring.domain.cbt.repository.QuestionRepository;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectAnswerDTO;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectDTO;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectQuestionDTO;
import kr.yuhancert.spring.domain.user.entity.UserAnswer;
import kr.yuhancert.spring.domain.user.repository.UserAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCbtService {

    private final JwtService jwtService;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public UserCbtService(
            JwtService __jwtService,
            UserAnswerRepository __userAnswerRepository,
            QuestionRepository __questionRepository,
            AnswerRepository __answerRepository
            ) {
        this.jwtService = __jwtService;
        this.userAnswerRepository = __userAnswerRepository;
        this.questionRepository = __questionRepository;
        this.answerRepository = __answerRepository;
    }

    public UserIncorrectDTO getUserIncorrect(Long __certId, HttpServletRequest request) {
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        List<UserAnswer> userAnswerList = userAnswerRepository.findByCertificate_Id(__certId);

        Certificate cert = userAnswerList.get(0).getCertificate();

        Map<Long, UserAnswer> userAnswerMap = userAnswerList.stream()
                .collect(Collectors.toMap(
                        ua -> ua.getAnswer().getId(),  // FK
                        ua -> ua
                ));

        Set<Long> questionIds = new HashSet<>();

        for (Map.Entry<Long, UserAnswer> uam : userAnswerMap.entrySet()) {

            Long id = uam.getValue().getAnswer().getQuestion().getId();

            questionIds.add(id);
        }

        List<Question> questionList = questionRepository.findByIdIn(questionIds.stream().toList());

        List<Answer> answerList = answerRepository.findByQuestionIn(questionList.stream().toList());

        Map<Long, List<UserIncorrectAnswerDTO>> userIncorrectAnswerDTOMapList = new HashMap<>();

        for (Answer a : answerList) {

            UserIncorrectAnswerDTO dto = new UserIncorrectAnswerDTO();

            dto.setAnswer_id(a.getId());
            dto.setContent(a.getContent());
            dto.setBool(a.getBool());
            dto.setImg(a.getImg());

            userIncorrectAnswerDTOMapList
                    .computeIfAbsent(a.getQuestion().getId(), k -> new ArrayList<>())
                    .add(dto);
        }

        //이건 gpt에게 맡겨봤는데 맛있네
        for (Map.Entry<Long, List<UserIncorrectAnswerDTO>> entry : userIncorrectAnswerDTOMapList.entrySet()) {

            List<UserIncorrectAnswerDTO> list = entry.getValue();

            // true(정답)
            List<UserIncorrectAnswerDTO> correctList = list.stream()
                    .filter(UserIncorrectAnswerDTO::getBool)
                    .collect(Collectors.toList());

            // false(오답)
            List<UserIncorrectAnswerDTO> incorrectList = list.stream()
                    .filter(dto -> !dto.getBool())
                    .collect(Collectors.toList());

            // 정답 1개 랜덤 선택
            Collections.shuffle(correctList);
            UserIncorrectAnswerDTO correct = correctList.isEmpty() ? null : correctList.get(0);

            // 오답 3개 랜덤 선택
            Collections.shuffle(incorrectList);
            List<UserIncorrectAnswerDTO> incorrectPick = incorrectList.stream()
                    .limit(3)
                    .toList();

            // 최종 4개 구성
            List<UserIncorrectAnswerDTO> finalList = new ArrayList<>();
            if (correct != null) finalList.add(correct);
            finalList.addAll(incorrectPick);

            // 섞기
            Collections.shuffle(finalList);

            // Map에 다시 저장
            entry.setValue(finalList);
        }

        List<UserIncorrectQuestionDTO> userIncorrectQuestionDTOList = new ArrayList<>();

        for (Question q : questionList) {
            UserIncorrectQuestionDTO dto = new UserIncorrectQuestionDTO();

            List<UserIncorrectAnswerDTO> incorrectList =
                    userIncorrectAnswerDTOMapList.getOrDefault(q.getId(), new ArrayList<>());

            dto.setQuestion_id(q.getId());
            dto.setText(q.getText());
            dto.setContent(q.getContent());
            dto.setImg(q.getImg());
            dto.setUserIncorrectAnswerDTOList(incorrectList);

            userIncorrectQuestionDTOList.add(dto);
        }

        return new UserIncorrectDTO(
                cert.getId(),
                cert.getCertificateName(),
                userIncorrectQuestionDTOList
        );
    }
}
