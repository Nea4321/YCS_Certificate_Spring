package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.DeptMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptMapRepository extends JpaRepository<DeptMap, Long> {

}
