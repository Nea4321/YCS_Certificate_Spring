package kr.yuhancert.spring.domain.login.repository;

import kr.yuhancert.spring.domain.login.entity.UserData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    @EntityGraph(attributePaths = {"user"})
    UserData findByUserUserId(Long userId);
}
