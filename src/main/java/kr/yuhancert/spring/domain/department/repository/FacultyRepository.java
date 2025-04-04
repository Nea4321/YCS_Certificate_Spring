package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

}
