package kr.yuhancert.spring.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.yuhancert.spring.domain.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "user_favorite")
@NoArgsConstructor
@AllArgsConstructor
public class UserFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 타입으로
     * department, certificate 각각 학과, 자격증 페이지에서  즐겨찾기
     * cancel 학과 즐겨찾기를 했지만 일부 자격증은 보고싶지 않을때
     */
    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private String type;

    @NotNull
    @Column(name = "type_id", nullable = false)
    private Long typeId;

}
