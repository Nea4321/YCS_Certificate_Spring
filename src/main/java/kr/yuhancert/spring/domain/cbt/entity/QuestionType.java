package kr.yuhancert.spring.domain.cbt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "question_type")
@NoArgsConstructor
@AllArgsConstructor
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_type_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "question_info_id", nullable = false)
    private QuestionInfo questionInfo;

    @NotNull
    @Column(name = "question_type_name", nullable = false, length = Integer.MAX_VALUE)
    private String questionTypeName;

    @NotNull
    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "priority")
    private Integer priority;

}
