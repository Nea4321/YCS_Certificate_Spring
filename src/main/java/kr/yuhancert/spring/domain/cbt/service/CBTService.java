package kr.yuhancert.spring.domain.cbt.service;

import kr.yuhancert.spring.domain.cbt.dto.CBTDTO;
import kr.yuhancert.spring.domain.cbt.dto.QuestionInfoDTO;
import kr.yuhancert.spring.domain.cbt.entity.QuestionInfo;
import kr.yuhancert.spring.domain.cbt.mapper.CBTMapper;
import kr.yuhancert.spring.domain.cbt.repository.*;
import kr.yuhancert.spring.domain.certificate.dto.CertificateDTO;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.service.CertificateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CBTService {

    private final CertificateService certificateService;

    private final QuestionInfoRepository questionInfoRepository;
    private final QuestionTypeRepository questionTypeRepositoty;
    private final QuestionRepository  questionRepository;
    private final AnswerRepository answerRepository;
    private final PreivousRepository preivousRepository;

    private final CBTMapper cbtMapper;

    public CBTService(CertificateService __certificateService,

                      QuestionInfoRepository __questionInfoRepository,
                      QuestionTypeRepository __questionTypeRepository,
                      QuestionRepository __questionRepository,
                      AnswerRepository __answerRepository,
                      PreivousRepository __previousRepository,

                      CBTMapper __cbtMapper
    ) {
        this.certificateService = __certificateService;

        this.questionInfoRepository = __questionInfoRepository;
        this.questionTypeRepositoty = __questionTypeRepository;
        this.questionRepository = __questionRepository;
        this.answerRepository = __answerRepository;
        this.preivousRepository = __previousRepository;

        this.cbtMapper = __cbtMapper;
    }

    public List<CertificateDTO> getCBTList() {
        List<Certificate> certificateList = questionInfoRepository.findDistinctByCertificate();
        return certificateService.getCertificate(certificateList);
    }

    public List<CBTDTO> getCBTDTOList(Long __id) {
        List<QuestionInfo> questionInfoList = questionInfoRepository.findAllByCertificate_Id(__id);
        return cbtMapper.toCBTDTOList(questionInfoList);
    }

    public Boolean isMain(Long __id) {
        QuestionInfo questionInfo = questionInfoRepository.findById(__id).orElse(null);
        if(questionInfo == null)
            throw new IllegalStateException("QuestionInfo not found");
        return questionInfo.getMain();
    }

//    public QuestionInfoDTO getQuestion(Long __id) {
//
//    }

}
