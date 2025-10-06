package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "organization")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "organization_name", nullable = false, length = Integer.MAX_VALUE)
    private String organizationName;

    @OneToMany(mappedBy = "organization")
    private Set<Certificate> certificates = new LinkedHashSet<>();

}
