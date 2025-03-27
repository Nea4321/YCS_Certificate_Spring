package kr.yuhancert.spring.domain.department.repository;

import kr.yuhancert.spring.domain.department.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Integer> {
    Optional<Major> findByMajorName(String majorName);
}
