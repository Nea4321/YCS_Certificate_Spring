package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CertDataRepository extends JpaRepository<CertData, Long> {
    @EntityGraph(attributePaths = {"certificate"})
    Optional<CertData> findById(Long id);

    @Query("SELECT new kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO(c.id, c.certificateName, c.schedule) " +
            "FROM CertData c WHERE c.id IN :ids")
    List<ScheduleDTO> findSchedulesByIds(@Param("ids") List<Long> ids);

}