package kr.yuhancert.spring.domain.user.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavoriteDTO {
    private String type;
    private Long type_id;
    private String name;
}
