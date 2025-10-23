package kr.yuhancert.spring.domain.user.repository;

import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<UserFavorite> findAllByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"user"})
    List<UserFavorite> findAllByUser_IdAndTypeNot(Long userId, String type);

    @EntityGraph(attributePaths = {"user"})
    Optional<UserFavorite> findByUser_IdAndTypeAndTypeId(Long userId, String type, Long typeId);

    @EntityGraph(attributePaths = {"user"})
    boolean existsByUser_IdAndTypeAndTypeId(Long userId, String type, Long typeId);

    @EntityGraph(attributePaths = {"user"})
    boolean existsByUser_IdAndType(Long userId, String type);

    void deleteAllByUser_IdAndType(Long userId, String type);

    @EntityGraph(attributePaths = {"user"})
    List<UserFavorite> findAllByUser_IdAndType(Long userId, String type);
}
