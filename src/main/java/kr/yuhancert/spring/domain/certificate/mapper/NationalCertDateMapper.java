package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.NationalCertDateDTO;
import kr.yuhancert.spring.domain.certificate.entity.NationalCertDate;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



@Mapper(componentModel = "spring")
public interface NationalCertDateMapper {

    default List<NationalCertDateDTO> toScheduleDTOList(List<NationalCertDate> entities) {
        List<NationalCertDateDTO> result = new ArrayList<>();
        long today = Long.parseLong(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        for (NationalCertDate entity : entities) {
            String name = entity.getDescription();
            // 각 일정별로 오늘 날짜에 해당하는지 확인하고, 해당하면 리스트에 추가
            checkAndAdd(result, name, "필기시험 원서접수", today, entity.getDocRegStartDt(), entity.getDocRegEndDt());
            checkAndAdd(result, name, "필기시험", today, entity.getDocExamStartDt(), entity.getDocExamEndDt());
            checkAndAdd(result, name, "필기시험 합격자 발표", today, entity.getDocPassDt(), entity.getDocPassDt());
            checkAndAdd(result, name, "실기(작업)/면접 시험 원서접수", today, entity.getPracRegStartDt(), entity.getPracRegEndDt());
            checkAndAdd(result, name, "실기(작업)/면접 시험", today, entity.getPracExamStartDt(), entity.getPracExamEndDt());
            checkAndAdd(result, name, "실기(작업)/면접 시험 합격자 발표", today, entity.getPracPassDt(), entity.getPracPassDt());
        }

        return result;
    }
    private void checkAndAdd(List<NationalCertDateDTO> result, String name, String content, long today, String startDateStr, String endDateStr) {
        if (startDateStr == null || startDateStr.isBlank() || endDateStr == null || endDateStr.isBlank()) {
            return;
        }
        try {
            long start = Long.parseLong(startDateStr.trim());
            long end = Long.parseLong(endDateStr.trim());
            if (today >= start && today <= end) {
                result.add(new NationalCertDateDTO(name, content));
            }
        } catch (NumberFormatException ignored) {
        }
    }
}