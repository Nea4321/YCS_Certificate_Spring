package kr.yuhancert.spring.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

///  백엔드에서 프론트 엔드로 보내는 cbt 기록 정보
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCbtHistoryResponseDTO {
    private Long user_cbt_history_id;
    private Long user_id;
    private Long certificate_id;
    private String certificate_name;
    private Integer score;
    private Integer correct_count;
    private Long previous_id;
    private Instant created_at;
    private Integer left_time;
}
