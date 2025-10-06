package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "cert_data")
public class CertData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @NotNull
    @Column(name = "certificate_name", nullable = false, length = Integer.MAX_VALUE)
    private String certificateName;

    @Column(name = "basic_info")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> basicInfo;

    @Column(name = "schedule")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> schedule;

    @Column(name = "other_info")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> otherInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "organization_id")
    private Organization organization;

}