package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "certificate")
public class Certificate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "certificate_name", nullable = false)
    private String certificateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "jmcd")
    private NationalCert jmcd;

    @OneToOne(mappedBy = "certificate")
    private CertData certDatum;

    @OneToMany(mappedBy = "certificate")
    private Set<DeptCert> deptCerts = new LinkedHashSet<>();

}