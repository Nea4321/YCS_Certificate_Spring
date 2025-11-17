package kr.yuhancert.spring.domain.user.repository;

import kr.yuhancert.spring.domain.user.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
}
