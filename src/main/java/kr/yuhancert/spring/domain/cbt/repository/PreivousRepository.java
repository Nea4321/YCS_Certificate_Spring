package kr.yuhancert.spring.domain.cbt.repository;

import kr.yuhancert.spring.domain.cbt.entity.Previous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreivousRepository extends JpaRepository<Previous, Long> {
    Previous findByTypeAndTypeId(String type, Long typeId);
}
