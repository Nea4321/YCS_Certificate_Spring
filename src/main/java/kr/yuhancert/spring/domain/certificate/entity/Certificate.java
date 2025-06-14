package kr.yuhancert.spring.domain.certificate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "certificate")
public class Certificate {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jmcd")
    private NationalCert jmcd;

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