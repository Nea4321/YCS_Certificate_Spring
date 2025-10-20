package kr.yuhancert.spring.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity(name = "LoginUser")
@Table(name = "\"user\"") // PostgreSQL에서 user는 예약어라서 쌍따옴표 필요
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Size(max = 255)
    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Size(max = 255)
    @Column(name = "user_password")
    private String userPassword;

    @Size(max = 255)
    @NotNull
    @Column(name = "social_type", nullable = false)
    private String socialType;

    @Size(max = 255)
    @NotNull
    @ColumnDefault("'normal'")
    @Column(name = "user_role", nullable = false)
    private String userRole;

}
