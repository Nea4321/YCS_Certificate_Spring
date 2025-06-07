package kr.yuhancert.spring.domain.department.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonIgnore
    private DeptMap deptMap;

    @Column(name = "asdf")
    private String asdf;

}
