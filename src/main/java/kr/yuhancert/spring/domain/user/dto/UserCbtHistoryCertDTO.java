package kr.yuhancert.spring.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCbtHistoryCertDTO {
    private Long certificate_id;
    private String certificate_name;
    private List<UserCbtHistoryListDTO> list;
}
