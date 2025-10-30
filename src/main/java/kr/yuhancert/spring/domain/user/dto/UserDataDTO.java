package kr.yuhancert.spring.domain.user.dto;

import kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {
    private List<ScheduleDTO> scehdule;
}
