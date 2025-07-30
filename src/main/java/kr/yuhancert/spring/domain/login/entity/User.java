package kr.yuhancert.spring.domain.login.entity;

import jakarta.persistence.*;
import kr.yuhancert.spring.domain.login.type.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

//유저 정보
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String userId;

    @Column(length = 50)
    private String userName;

    @Column(length = 50)
    private String userEmail;

    @Column(columnDefinition = "ENUM('KAKAO', 'NAVER', 'GITHUB', 'GOOGLE', 'NORMAL') DEFAULT 'NORMAL'")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

}
