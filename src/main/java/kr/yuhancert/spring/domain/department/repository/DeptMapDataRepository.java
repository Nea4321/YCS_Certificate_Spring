package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.DeptMapData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptMapDataRepository extends JpaRepository<DeptMapData, Long> {

}
