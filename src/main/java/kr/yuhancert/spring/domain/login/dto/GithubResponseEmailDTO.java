package kr.yuhancert.spring.domain.login.dto;

import lombok.Getter;

@Getter
public class GithubResponseEmailDTO {
    private String email;
    private boolean primary;
    private boolean verified;
}
