package kr.yuhancert.spring.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

///  프론트엔드 에서 백엔드로 보내는 cbt 기록 저장 데이터
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCbtHistoryDTO {
    private Long certificate_id;
    private Integer score;
    private Integer correct_Count;
    private Integer left_time;
//    private Long privious_id;
}
