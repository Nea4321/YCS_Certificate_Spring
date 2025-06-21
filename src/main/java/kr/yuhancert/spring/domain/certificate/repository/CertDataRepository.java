package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.entity.CertData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertDataRepository extends JpaRepository<CertData, Long> {
    @EntityGraph(attributePaths = {"certificate"})
    Optional<CertData> findById(Long id);
}