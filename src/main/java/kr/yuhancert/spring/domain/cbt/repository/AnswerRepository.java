package kr.yuhancert.spring.domain.cbt.repository;

import kr.yuhancert.spring.domain.cbt.entity.Answer;
import kr.yuhancert.spring.domain.cbt.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @EntityGraph(attributePaths = {"question"})
    List<Answer> findByQuestionIn(List<Question> questions);
}
