package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @EntityGraph(attributePaths = {"organization"})
    List<Certificate> findAll();

    @EntityGraph(attributePaths = {"organization"})
    List<Certificate> findAllByIdIn(List<Long> ids);
}