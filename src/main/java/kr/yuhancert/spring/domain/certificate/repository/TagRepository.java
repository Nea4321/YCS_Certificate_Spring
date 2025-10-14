package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findAll();
}
