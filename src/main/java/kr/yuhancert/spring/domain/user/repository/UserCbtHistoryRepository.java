package kr.yuhancert.spring.domain.user.repository;

import kr.yuhancert.spring.domain.user.entity.UserCbtHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCbtHistoryRepository extends JpaRepository<UserCbtHistory, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<UserCbtHistory> findAllByUser_Id(Long userId);

    List<UserCbtHistory> findAllByUser_IdOrderByCreatedAtAsc(Long userId);
}
