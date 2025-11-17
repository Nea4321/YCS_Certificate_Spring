package kr.yuhancert.spring.domain.cbt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "question_info")
@NoArgsConstructor
@AllArgsConstructor
public class QuestionInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quest_info_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @NotNull
    @Column(name = "question_info_name", nullable = false, length = Integer.MAX_VALUE)
    private String questionInfoName;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "type_cnt", nullable = false)
    private Integer typeCnt;

    @NotNull
    @Column(name = "size", nullable = false)
    private Integer size;

    @NotNull
    @Column(name = "main", nullable = false)
    private Boolean main = false;

}
