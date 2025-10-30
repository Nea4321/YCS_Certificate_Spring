package kr.yuhancert.spring.domain.auth.repository;

import kr.yuhancert.spring.domain.auth.entity.SocialType;
import kr.yuhancert.spring.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("loginUserRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserEmailAndSocialType(String email, SocialType socialType);
    Optional<User> findBySocialType(SocialType socialType);
}
