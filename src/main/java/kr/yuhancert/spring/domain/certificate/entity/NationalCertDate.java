package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "national_cert_date")
public class NationalCertDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "\"implYy\"", length = Integer.MAX_VALUE)
    private String implYy;

    @Column(name = "\"implSeq\"", length = Integer.MAX_VALUE)
    private String implSeq;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "\"docRegStartDt\"", length = Integer.MAX_VALUE)
    private String docRegStartDt;

    @Column(name = "\"docRegEndDt\"", length = Integer.MAX_VALUE)
    private String docRegEndDt;

    @Column(name = "\"docExamStartDt\"", length = Integer.MAX_VALUE)
    private String docExamStartDt;

    @Column(name = "\"docExamEndDt\"", length = Integer.MAX_VALUE)
    private String docExamEndDt;

    @Column(name = "\"docPassDt\"", length = Integer.MAX_VALUE)
    private String docPassDt;

    @Column(name = "\"pracRegStartDt\"", length = Integer.MAX_VALUE)
    private String pracRegStartDt;

    @Column(name = "\"pracRegEndDt\"", length = Integer.MAX_VALUE)
    private String pracRegEndDt;

    @Column(name = "\"pracExamStartDt\"", length = Integer.MAX_VALUE)
    private String pracExamStartDt;

    @Column(name = "\"pracExamEndDt\"", length = Integer.MAX_VALUE)
    private String pracExamEndDt;

    @Column(name = "\"pracPassDt\"", length = Integer.MAX_VALUE)
    private String pracPassDt;

    @Column(name = "\"qualgbCd\"", length = Integer.MAX_VALUE)
    private String qualgbCd;

    @Column(name = "\"qualgbNm\"", length = Integer.MAX_VALUE)
    private String qualgbNm;

}
