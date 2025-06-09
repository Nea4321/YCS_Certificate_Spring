package kr.yuhancert.spring.domain.certificate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CertData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "certificate_id", nullable = false)
    @JsonIgnore
    private Certificate certificate;

    @Column(name = "\"implYy\"")
    private Integer implYy;

    @Column(name = "\"implSeq\"")
    private Integer implSeq;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "\"docRegStartDt\"")
    private Long docRegStartDt;

    @Column(name = "\"docRegEndDt\"")
    private Long docRegEndDt;

    @Column(name = "\"docExamStartDt\"")
    private Long docExamStartDt;

    @Column(name = "\"docExamEndDt\"")
    private Long docExamEndDt;

    @Column(name = "\"docPassDt\"")
    private Long docPassDt;

    @Column(name = "\"pracRegStartDt\"")
    private Long pracRegStartDt;

    @Column(name = "\"pracRegEndDt\"")
    private Long pracRegEndDt;

    @Column(name = "\"pracExamStartDt\"")
    private Long pracExamStartDt;

    @Column(name = "\"pracExamEndDt\"")
    private Long pracExamEndDt;

    @Column(name = "\"pracPassDt\"")
    private Long pracPassDt;

}