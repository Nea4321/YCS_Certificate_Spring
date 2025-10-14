package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.entity.TagMap;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagMapRepository extends JpaRepository<TagMap,Long> {
    @EntityGraph(attributePaths = {"certificate", "tag"})
    List<TagMap> findAll();
}
