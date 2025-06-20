package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.DeptMapData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptMapDataRepository extends JpaRepository<DeptMapData, Long> {
    @EntityGraph(attributePaths = {"deptMap", "deptMap.major", "deptMap.department", "deptMap.faculty"})
    List<DeptMapData> findAll();
}
