package kr.yuhancert.spring.domain.certificate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "tag_name", nullable = false, length = Integer.MAX_VALUE)
    private String tagName;

    @Column(name = "color", length = Integer.MAX_VALUE)
    private String color;

}
