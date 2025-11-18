package kr.yuhancert.spring.domain.user.repository;

import kr.yuhancert.spring.domain.cbt.entity.Previous;
import kr.yuhancert.spring.domain.user.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByPrevious(Previous previous);

    List<UserAnswer> findByCertificate_Id(Long certificateId);

    List<UserAnswer> findByUser_IdAndCertificate_Id(Long userId, Long certificateId);
}
