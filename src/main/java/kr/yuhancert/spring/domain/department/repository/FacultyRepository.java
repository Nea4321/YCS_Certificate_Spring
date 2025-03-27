package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    Optional<Faculty> findByFacultyName(String facultyName);

}
