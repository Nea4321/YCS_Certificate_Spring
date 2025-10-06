package kr.yuhancert.spring.domain.department.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "dept_map_data")
@NoArgsConstructor
public class DeptMapData {
    @Id
    @Column(name = "dept_map_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "dept_map_id")
    private DeptMap deptMap;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> description;

}
