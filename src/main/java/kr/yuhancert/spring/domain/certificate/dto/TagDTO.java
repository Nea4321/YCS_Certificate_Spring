package kr.yuhancert.spring.domain.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    private Long tag_id;
    private String tag_name;
    private String color;
}
