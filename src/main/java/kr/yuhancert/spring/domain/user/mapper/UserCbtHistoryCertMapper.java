package kr.yuhancert.spring.domain.user.mapper;

import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryCertDTO;
import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryListDTO;
import kr.yuhancert.spring.domain.user.entity.UserCbtHistory;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
@Qualifier("userCbtHistoryMapper")
public interface UserCbtHistoryCertMapper {

    default List<UserCbtHistoryCertDTO> toUserCbtHistoryCertDTOList(Map<Long, List<UserCbtHistory>> __history) {

        List<UserCbtHistoryCertDTO>  certDTOS = new ArrayList<>();

        for (Map.Entry<Long, List<UserCbtHistory>> hm : __history.entrySet()) {

            List<UserCbtHistoryListDTO> listDTOS = toUserCbtHistoryListDTOList(hm.getValue());

            Certificate certificate = hm.getValue().get(0).getCertificate();

            UserCbtHistoryCertDTO certDTO =  new UserCbtHistoryCertDTO(
                    certificate.getId(),
                    certificate.getCertificateName(),
                    listDTOS
            );

            certDTOS.add(certDTO);
        }

        return certDTOS;
    }

    private List<UserCbtHistoryListDTO> toUserCbtHistoryListDTOList(List<UserCbtHistory> __history) {

        List<UserCbtHistoryListDTO> listDTOS = new ArrayList<>();

        for (UserCbtHistory h : __history) {
            UserCbtHistoryListDTO listDTO = new UserCbtHistoryListDTO(
                    h.getPrevious().getId(),
                    h.getPrevious().getType(),
                    h.getScore(),
                    h.getCorrectCount(),
                    h.getCreatedAt().toInstant(),
                    h.getLeftTime()
                    );

            listDTOS.add(listDTO);
        }

        return listDTOS;
    }
}
