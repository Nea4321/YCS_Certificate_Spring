package kr.yuhancert.spring.domain.login.repository;

import kr.yuhancert.spring.domain.login.entity.UserFavorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<UserFavorite> findAllByUserUserId(Long userId);
}
