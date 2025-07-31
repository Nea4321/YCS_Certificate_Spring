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

// 유저 정보
// 해당 엔티티는 github 소셜 프로젝트에 있던 엔티티를 가져온거 나중에 DB 생기면 수정할 예정
// 테이블 없어서 오류 생기는데 실행은 됨.
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
