package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.entity.CertDept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertDeptRepository extends JpaRepository<CertDept, Long> {

}