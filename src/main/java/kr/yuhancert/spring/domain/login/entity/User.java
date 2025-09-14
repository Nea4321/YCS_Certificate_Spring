package kr.yuhancert.spring.domain.login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"user\"") // PostgreSQL에서 user는 예약어라서 쌍따옴표 필요
@IdClass(UserId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_role", nullable = false)
    private String userRole = "normal";
}
