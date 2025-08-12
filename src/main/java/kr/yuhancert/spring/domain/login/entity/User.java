package kr.yuhancert.spring.domain.login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"user\"") // 예약어니까 반드시 큰따옴표로 감싸야 함
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email", unique = true)
    private String userEmail;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_password")
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", columnDefinition = "socialType")
    private SocialType socialType;
}
