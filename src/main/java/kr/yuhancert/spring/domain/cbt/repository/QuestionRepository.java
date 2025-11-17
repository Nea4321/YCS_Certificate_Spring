package kr.yuhancert.spring.domain.cbt.repository;

import kr.yuhancert.spring.domain.cbt.entity.Question;
import kr.yuhancert.spring.domain.cbt.entity.QuestionType;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @EntityGraph(attributePaths = {"certificate", "question_type"})
    List<Question> findByCertificate(Certificate cert);

    List<Question> findByIdIn(List<Long> ids);

    List<Question> findByQuestionType(QuestionType questionType);
}
