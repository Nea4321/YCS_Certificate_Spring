package kr.yuhancert.spring.domain.login.repository;

import kr.yuhancert.spring.domain.login.entity.SocialType;
import kr.yuhancert.spring.domain.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserEmailAndSocialType(String email, SocialType socialType);
    Optional<User> findBySocialType(SocialType socialType);
}
