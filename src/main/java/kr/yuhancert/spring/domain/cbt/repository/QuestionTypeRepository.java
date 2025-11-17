package kr.yuhancert.spring.domain.cbt.repository;

import kr.yuhancert.spring.domain.cbt.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {
}
