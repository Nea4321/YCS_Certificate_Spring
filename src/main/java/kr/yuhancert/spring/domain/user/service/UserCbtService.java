package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.cbt.dto.*;
import kr.yuhancert.spring.domain.cbt.entity.*;
import kr.yuhancert.spring.domain.cbt.mapper.PreviousMapper;
import kr.yuhancert.spring.domain.cbt.repository.*;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectAnswerDTO;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectDTO;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectQuestionDTO;
import kr.yuhancert.spring.domain.user.entity.UserAnswer;
import kr.yuhancert.spring.domain.user.repository.UserAnswerRepository;
import kr.yuhancert.spring.domain.user.repository.UserCbtHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCbtService {

    private final JwtService jwtService;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionInfoRepository questionInfoRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final AnswerRepository answerRepository;
    private final PreivousRepository preivousRepository;
    private final PreviousMapper previousMapper;


    public UserCbtService(
            JwtService __jwtService,
            UserAnswerRepository __userAnswerRepository,
            QuestionRepository __questionRepository,
            QuestionInfoRepository __questionInfoRepository,
            QuestionTypeRepository __questionTypeRepository,
            AnswerRepository __answerRepository,
            PreivousRepository __previousRepository,
            PreviousMapper previousMapper,
            UserCbtHistoryRepository __userCbtHistoryRepository) {
        this.jwtService = __jwtService;
        this.userAnswerRepository = __userAnswerRepository;
        this.questionRepository = __questionRepository;
        this.questionInfoRepository = __questionInfoRepository;
        this.questionTypeRepository = __questionTypeRepository;
        this.answerRepository = __answerRepository;
        this.preivousRepository = __previousRepository;
        this.previousMapper = previousMapper;
    }

    public PreviousDTO getRandomQuestion(Long __questionInfoId, HttpServletRequest request) {
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        QuestionInfo questionInfo = questionInfoRepository.findById(__questionInfoId)
                .orElseThrow(() -> new RuntimeException("questionInfo not found"));

        QuestionInfoDTO questionInfoDTO = generateQuestion(questionInfo);

        Previous addPrevious = new Previous();

        addPrevious.setType(PreviousType.user.toString());
        addPrevious.setTypeId(userId);
        addPrevious.setList(questionInfoDTO);

        Previous previous = preivousRepository.save(addPrevious);

        return previousMapper.toPreviousDTO(previous);
    }

    @Transactional
    public PreviousDTO getIncorrect(Long certId, HttpServletRequest request) {
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        QuestionInfoDTO questionInfoDTO = generateIncorrect(certId, userId);

        Previous addPrevious = new Previous();
        addPrevious.setType(PreviousType.incorrect.toString());
        addPrevious.setTypeId(userId);
        addPrevious.setList(questionInfoDTO);

        Previous previous = preivousRepository.save(addPrevious);
        return previousMapper.toPreviousDTO(previous);
    }

    private QuestionInfoDTO generateQuestion(QuestionInfo __questionInfo) {

        List<QuestionType> questionTypeList = questionTypeRepository.findByQuestionInfo(__questionInfo);

        Map<Long, List<Question>> questionListMap = new HashMap<>();

        for (QuestionType qt : questionTypeList) {
            List<Question> allQuestion = questionRepository.findByQuestionType(qt);

            Collections.shuffle(allQuestion);

            int limit = Math.min(qt.getSize(), allQuestion.size());
            List<Question> questionList = allQuestion.subList(0, limit);

            questionListMap.put(qt.getId(), questionList);
        }

        questionTypeList.sort(Comparator.comparing(
                qt -> qt.getPriority() == null ? Integer.MAX_VALUE : qt.getPriority()
        ));

        List<Question> questionList = questionListMap.values().stream()
                .flatMap(List::stream)
                .toList();

        List<Answer> answerList = answerRepository.findByQuestionIn(questionList);

        //답안 DTO 조립
        Map<Long, List<AnswerDTO>> answerListMap = new HashMap<>();

        for (Answer a : answerList) {

            AnswerDTO answerDTO = new AnswerDTO();

            answerDTO.setAnswer_id(a.getId());
            answerDTO.setQuestion_id(a.getQuestion().getId());
            answerDTO.setBool(a.getBool());
            answerDTO.setContent(a.getContent());
            answerDTO.setImg(a.getImg());
            answerDTO.setSolution(a.getSolution() == null ? "" : a.getSolution());

            answerListMap.computeIfAbsent(a.getQuestion().getId(), k -> new ArrayList<>()).add(answerDTO);
        }

        for (Map.Entry<Long, List<AnswerDTO>> entry : answerListMap.entrySet()) {

            List<AnswerDTO> list = entry.getValue();

            List<AnswerDTO> correctList = new ArrayList<>(list.stream()
                    .filter(AnswerDTO::getBool)
                    .toList());

            List<AnswerDTO> incorrectList = new ArrayList<>(list.stream()
                    .filter(dto -> !dto.getBool())
                    .toList());

            Collections.shuffle(correctList);
            AnswerDTO correct = correctList.get(0);

            Collections.shuffle(incorrectList);
            List<AnswerDTO> incorrect = incorrectList.stream()
                    .limit(3)
                    .toList();

            List<AnswerDTO> finalList = new ArrayList<>();
            finalList.add(correct);
            finalList.addAll(incorrect);
            Collections.shuffle(finalList);

            entry.setValue(finalList);
        }

        //문제 DTO 조립
        Map<Long, List<QuestionDTO>> questionDTOListMap = new HashMap<>();

        for (Map.Entry<Long, List<Question>> entry : questionListMap.entrySet()) {

            Long questionTypeId = entry.getKey();
            List<Question> qList = entry.getValue();

            List<QuestionDTO> dtoList = new ArrayList<>();

            for (Question q : qList) {

                QuestionDTO dto = new QuestionDTO();
                dto.setQuestion_id(q.getId());
                dto.setText(q.getText());
                dto.setContent(q.getContent());
                dto.setImg(q.getImg());

                List<AnswerDTO> answerDTOList = answerListMap.get(q.getId());
                dto.setAnswers(answerDTOList);

                dtoList.add(dto);
            }

            questionDTOListMap.put(questionTypeId, dtoList);
        }

        //문제 타입 DTO 조립
        List<QuestionTypeDTO> questionTypeDTOList = new ArrayList<>();

        for (QuestionType qt : questionTypeList) {

            QuestionTypeDTO typeDTO = new QuestionTypeDTO();

            typeDTO.setQuestion_type_id(qt.getId());
            typeDTO.setQuestion_type_name(qt.getQuestionTypeName());

            List<QuestionDTO> questionDTOList = questionDTOListMap.get(qt.getId());

            typeDTO.setQuestions(questionDTOList);

            questionTypeDTOList.add(typeDTO);
        }

        long questionNum = 1L;

        for (QuestionType qt : questionTypeList) {

            List<QuestionDTO> qList = questionDTOListMap.get(qt.getId());
            if (qList == null) continue;

            for (QuestionDTO qdto : qList) {
                qdto.setQuestion_num(questionNum++);
            }
        }


        return new QuestionInfoDTO(
                __questionInfo.getId(),
                __questionInfo.getQuestionInfoName(),
                questionTypeDTOList
        );
    }


    private QuestionInfoDTO generateIncorrect(Long certId, Long userId) {

        List<UserAnswer> userAnswerList =
                userAnswerRepository.findByUser_IdAndCertificate_Id(userId, certId);

        if (userAnswerList.isEmpty()) {
            throw new IllegalArgumentException("해당 자격증 풀이 기록이 없습니다.");
        }

        Set<Long> wrongQuestionIds = userAnswerList.stream()
                .filter(ua -> ua.getBool() == null || Boolean.FALSE.equals(ua.getBool()))
                .map(ua -> {
                    if (ua.getQuestion() != null) return ua.getQuestion().getId();
                    if (ua.getAnswer() != null && ua.getAnswer().getQuestion() != null) return ua.getAnswer().getQuestion().getId();
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (wrongQuestionIds.isEmpty()) {
            throw new IllegalArgumentException("오답이 없습니다.");
        }

        List<Question> wrongQuestions = questionRepository.findByIdIn(new ArrayList<>(wrongQuestionIds));
        if (wrongQuestions.isEmpty()) {
            throw new IllegalArgumentException("오답 문제를 찾을 수 없습니다.");
        }

        Collections.shuffle(wrongQuestions);

        QuestionInfo questionInfo = wrongQuestions.get(0).getQuestionType().getQuestionInfo();

        List<Answer> answerList = answerRepository.findByQuestionIn(wrongQuestions);

        Map<Long, List<AnswerDTO>> answerListMap = new HashMap<>();
        for (Answer a : answerList) {
            AnswerDTO dto = new AnswerDTO();
            dto.setAnswer_id(a.getId());
            dto.setQuestion_id(a.getQuestion().getId());
            dto.setBool(a.getBool());
            dto.setContent(a.getContent());
            dto.setImg(a.getImg());
            dto.setSolution(a.getSolution() == null ? "" : a.getSolution());
            answerListMap.computeIfAbsent(a.getQuestion().getId(), k -> new ArrayList<>()).add(dto);
        }

        for (Map.Entry<Long, List<AnswerDTO>> entry : answerListMap.entrySet()) {
            List<AnswerDTO> list = entry.getValue();
            List<AnswerDTO> correctList = list.stream().filter(AnswerDTO::getBool).collect(Collectors.toList());
            List<AnswerDTO> incorrectList = list.stream().filter(a -> !a.getBool()).collect(Collectors.toList());

            if (correctList.isEmpty()) continue;

            Collections.shuffle(correctList);
            AnswerDTO correct = correctList.get(0);

            Collections.shuffle(incorrectList);
            List<AnswerDTO> pickedIncorrect = incorrectList.stream().limit(3).toList();

            List<AnswerDTO> finalList = new ArrayList<>();
            finalList.add(correct);
            finalList.addAll(pickedIncorrect);
            Collections.shuffle(finalList);

            entry.setValue(finalList);
        }

        Map<Long, List<QuestionDTO>> byTypeDTO = new HashMap<>();
        long num = 1L;

        for (Question q : wrongQuestions) {
            QuestionDTO qdto = new QuestionDTO();
            qdto.setQuestion_id(q.getId());
            qdto.setText(q.getText());
            qdto.setContent(q.getContent());
            qdto.setImg(q.getImg());
            qdto.setAnswers(answerListMap.getOrDefault(q.getId(), new ArrayList<>()));
            qdto.setQuestion_num(num++);

            Long typeId = q.getQuestionType().getId();
            byTypeDTO.computeIfAbsent(typeId, k -> new ArrayList<>()).add(qdto);
        }

        List<QuestionType> orderedTypes = wrongQuestions.stream()
                .map(Question::getQuestionType).distinct().sorted(Comparator.comparing(qt ->
                        qt.getPriority() == null ? Integer.MAX_VALUE : qt.getPriority()
                )).toList();

        List<QuestionTypeDTO> questionTypeDTOList = new ArrayList<>();
        for (QuestionType qt : orderedTypes) {
            QuestionTypeDTO typeDTO = new QuestionTypeDTO();
            typeDTO.setQuestion_type_id(qt.getId());
            typeDTO.setQuestion_type_name(qt.getQuestionTypeName());
            typeDTO.setQuestions(byTypeDTO.getOrDefault(qt.getId(), new ArrayList<>()));
            questionTypeDTOList.add(typeDTO);
        }

        return new QuestionInfoDTO(
                questionInfo.getId(),
                questionInfo.getQuestionInfoName(),
                questionTypeDTOList
        );
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
            dto.setSolution(a.getSolution() == null ? "" : a.getSolution());

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
