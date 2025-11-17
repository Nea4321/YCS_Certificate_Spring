package kr.yuhancert.spring.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCbtHistoryListDTO {
    private Long previous_id;
    private Integer score;
    private Integer correct_count;
    private Instant created_at;
    private Integer left_time;
}
