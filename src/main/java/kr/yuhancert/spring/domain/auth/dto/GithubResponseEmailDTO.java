package kr.yuhancert.spring.domain.auth.dto;

import lombok.Getter;

@Getter
public class GithubResponseEmailDTO {
    private String email;
    private boolean primary;
    private boolean verified;
}
