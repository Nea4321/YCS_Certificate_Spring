package kr.yuhancert.spring.domain.department.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Faculty {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "faculty_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "faculty_name", nullable = false, length = Integer.MAX_VALUE)
    private String facultyName;

    @OneToMany(mappedBy = "faculty")
    private Set<DeptMap> deptMaps = new LinkedHashSet<>();

}
