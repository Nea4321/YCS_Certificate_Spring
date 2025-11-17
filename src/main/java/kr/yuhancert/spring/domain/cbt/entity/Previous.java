package kr.yuhancert.spring.domain.cbt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "previous")
@NoArgsConstructor
@AllArgsConstructor
public class Previous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "previous_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "previous_name", nullable = false, length = Integer.MAX_VALUE)
    private String previousName;

    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private String type;

    @NotNull
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @NotNull
    @Column(name = "list", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> list;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}
