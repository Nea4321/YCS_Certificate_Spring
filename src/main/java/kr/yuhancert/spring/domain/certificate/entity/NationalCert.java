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
@Table(name = "national_cert")
public class NationalCert {

    @Id
    @Column(name = "jmcd", nullable = false, length = Integer.MAX_VALUE)
    private String jmcd;

    @NotNull
    @Column(name = "jmfldnm", nullable = false, length = Integer.MAX_VALUE)
    private String jmfldnm;

    @Column(name = "mdobligfldcd", length = Integer.MAX_VALUE)
    private String mdobligfldcd;

    @Column(name = "mdobligfldnm", length = Integer.MAX_VALUE)
    private String mdobligfldnm;

    @Column(name = "obligfldcd", length = Integer.MAX_VALUE)
    private String obligfldcd;

    @Column(name = "obligfldnm", length = Integer.MAX_VALUE)
    private String obligfldnm;

    @Column(name = "qualgbcd", length = Integer.MAX_VALUE)
    private String qualgbcd;

    @Column(name = "qualgbnm", length = Integer.MAX_VALUE)
    private String qualgbnm;

    @Column(name = "seriescd", length = Integer.MAX_VALUE)
    private String seriescd;

    @Column(name = "seriesnm", length = Integer.MAX_VALUE)
    private String seriesnm;

    @OneToMany(mappedBy = "jmcd")
    private Set<Certificate> certificates = new LinkedHashSet<>();

}
