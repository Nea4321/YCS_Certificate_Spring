package kr.yuhancert.spring.domain.user.repository;

import jakarta.validation.constraints.NotNull;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    // 트랜잭션 버그나서 이렇게 바꾼건데 왜 버그가 나고 이렇게 바꿔서 왜 고쳐진지는 이해 못했음...
    @Modifying
    @Transactional
    @Query("delete from UserFavorite u where u.user.id = :userId and u.type = :type")
    void deleteAllByUser_IdAndTypeIdAndType(@Param("userId") Long userId, @Param("type") String type);

    @EntityGraph(attributePaths = {"user"})
    List<UserFavorite> findAllByUser_IdAndType(Long userId, String type);

    List<UserFavorite> findAllByUser_IdAndTypeAndTypeId(long user_id, @NotNull String type, @NotNull Long typeId);

    void deleteAllByUser_IdAndTypeAndTypeId(Long userId, String string, Long typeId);
}
