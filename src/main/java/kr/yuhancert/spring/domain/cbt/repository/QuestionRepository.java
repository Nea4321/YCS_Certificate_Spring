package kr.yuhancert.spring.domain.cbt.repository;

import kr.yuhancert.spring.domain.cbt.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
