package kr.yuhancert.spring.domain.user.repository;

import kr.yuhancert.spring.domain.user.entity.UserCbtHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCbtHistoryRepository extends JpaRepository<UserCbtHistory, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<UserCbtHistory> findAllByUser_Id(Long userId);

//    @EntityGraph(attributePaths = {"user"})
//    @Query("SELECT distinct h.certificate.id from UserCbtHistory h where h.user.id = :userId")
//    List<UserCbtHistory> findDistinctCertificateByUser_Id(Long userId);

    List<UserCbtHistory> findAllByUser_IdOrderByCreatedAtAsc(Long userId);
}
