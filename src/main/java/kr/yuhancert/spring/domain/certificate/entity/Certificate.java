package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "certificate_name", nullable = false)
    private String certificateName;

    @OneToOne(mappedBy = "certificate")
    private CertData certDatum;

    @OneToMany(mappedBy = "certificate")
    private Set<CertDept> certDepts = new LinkedHashSet<>();

}