package kr.yuhancert.spring.domain.certificate.repository;

import kr.yuhancert.spring.domain.certificate.entity.CertData;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertDataRepository extends JpaRepository<CertData, Long> {

}