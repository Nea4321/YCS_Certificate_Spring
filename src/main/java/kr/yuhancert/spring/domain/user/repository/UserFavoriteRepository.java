package kr.yuhancert.spring.domain.user.repository;

import kr.yuhancert.spring.domain.user.entity.User;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<UserFavorite> findAllById(Long id);

    @EntityGraph(attributePaths = {"user"})
    Optional<UserFavorite> findByUserIdAndTypeAndTypeId(Long userId, String type, Long typeId);
}
