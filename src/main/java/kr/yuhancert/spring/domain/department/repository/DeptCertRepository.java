package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.DeptCert;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptCertRepository extends JpaRepository<DeptCert, Long> {
    @EntityGraph(attributePaths = {"certificate", "deptMap"})
    List<DeptCert> findAll();

    @EntityGraph(attributePaths = {"certificate", "deptMap"})
    List<DeptCert> findAllByDeptMapId(Long deptMapId);

    @EntityGraph(attributePaths = {"certificate", "deptMap"})
    List<DeptCert> findAllByDeptMapIdIn(List<Long> deptMapIds);
}
