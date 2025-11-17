package kr.yuhancert.spring.domain.cbt.repository;

import kr.yuhancert.spring.domain.cbt.entity.QuestionInfo;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionInfoRepository extends JpaRepository<QuestionInfo, Long> {

    @Query("SELECT distinct q.certificate from QuestionInfo q")
    List<Certificate> findDistinctByCertificate();

    @EntityGraph(attributePaths = {"certificate"})
    List<QuestionInfo> findAllByCertificate_Id(Long id);
}
