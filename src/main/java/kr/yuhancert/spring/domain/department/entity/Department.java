package kr.yuhancert.spring.domain.department.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "department")
@NoArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "department_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "department_name", nullable = false, length = Integer.MAX_VALUE)
    private String departmentName;

    @OneToMany(mappedBy = "department")
    private Set<DeptMap> deptMaps = new LinkedHashSet<>();

}
