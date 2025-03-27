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
@Table(name = "major")
@NoArgsConstructor
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "major_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "major_name", nullable = false, length = Integer.MAX_VALUE)
    private String majorName;

    @OneToMany(mappedBy = "major")
    private Set<DeptMap> deptMaps = new LinkedHashSet<>();

}
