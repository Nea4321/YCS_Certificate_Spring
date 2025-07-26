package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.DeptMap;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptMapRepository extends JpaRepository<DeptMap, Long> {
    @EntityGraph(attributePaths = {"faculty", "department", "major"})
    List<DeptMap> findAll();
}
