package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.Department;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.entity.Faculty;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeptMapRepository extends JpaRepository<DeptMap, Long> {
    @EntityGraph(attributePaths = {"faculty", "department", "major"})
    List<DeptMap> findAll();

    List<DeptMap> findByFaculty(Optional<Faculty> faculty);

    List<DeptMap> findByDepartment(Optional<Department> department);

    @EntityGraph(attributePaths = {"faculty", "department", "major"})
    List<DeptMap> findAllByIdIn(Collection<Long> ids); // 추가
}
