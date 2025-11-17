package kr.yuhancert.spring.domain.cbt.service;

import kr.yuhancert.spring.domain.cbt.dto.CBTDTO;
import kr.yuhancert.spring.domain.cbt.dto.PreviousDTO;
import kr.yuhancert.spring.domain.cbt.entity.Previous;
import kr.yuhancert.spring.domain.cbt.entity.PreviousType;
import kr.yuhancert.spring.domain.cbt.entity.QuestionInfo;
import kr.yuhancert.spring.domain.cbt.mapper.CBTMapper;
import kr.yuhancert.spring.domain.cbt.mapper.PreviousMapper;
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
    private final PreivousRepository preivousRepository;

    private final CBTMapper cbtMapper;
    private final PreviousMapper previousMapper;

    public CBTService(CertificateService __certificateService,

                      QuestionInfoRepository __questionInfoRepository,
                      PreivousRepository __previousRepository,

                      CBTMapper __cbtMapper,
                      PreviousMapper previousMapper) {
        this.certificateService = __certificateService;

        this.questionInfoRepository = __questionInfoRepository;
        this.preivousRepository = __previousRepository;

        this.cbtMapper = __cbtMapper;
        this.previousMapper = previousMapper;
    }

    public List<CertificateDTO> getCBTList() {
        List<Certificate> certificateList = questionInfoRepository.findDistinctByCertificate();
        return certificateService.getCertificate(certificateList);
    }

    public List<CBTDTO> getCBTDTOList(Long __id) {
        List<QuestionInfo> questionInfoList = questionInfoRepository.findAllByCertificate_Id(__id);
        return cbtMapper.toCBTDTOList(questionInfoList);
    }

    public PreviousDTO getPrevious(Long __id) {

        Previous previous = preivousRepository.findByTypeAndTypeId(PreviousType.question_info.toString(), __id);

        return previousMapper.toPreviousDTO(previous);
    }

}
