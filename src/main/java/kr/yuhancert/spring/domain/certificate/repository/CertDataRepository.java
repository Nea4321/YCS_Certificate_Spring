package kr.yuhancert.spring.domain.certificate.repository;

import jakarta.persistence.LockModeType;
import kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CertDataRepository extends JpaRepository<CertData, Long> {
    @EntityGraph(attributePaths = {"certificate", "organization"})
    Optional<CertData> findById(Long id);

    // 2) 저장(upsert)용: 동시성 방지 (버튼 중복/중복요청 대비)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CertData c where c.id = :id")
    Optional<CertData> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT new kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO(c.id, c.certificateName, c.schedule) " +
            "FROM CertData c WHERE c.id IN :ids")
    List<ScheduleDTO> findSchedulesByIds(@Param("ids") List<Long> ids);

}