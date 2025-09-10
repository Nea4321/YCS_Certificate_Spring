package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cert_data")
public class CertData {

    @Id
    @Column(name = "certificate_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Column(name = "infogb", length = Integer.MAX_VALUE)
    private String infogb;

    @Column(name = "contents", length = Integer.MAX_VALUE)
    private String contents;

}