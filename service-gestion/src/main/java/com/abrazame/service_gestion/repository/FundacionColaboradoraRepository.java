package com.abrazame.service_gestion.repository;
import com.abrazame.service_gestion.model.FundacionColaboradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FundacionColaboradoraRepository extends JpaRepository<FundacionColaboradora, Long> {
    List<FundacionColaboradora> findByActivoTrue();
}
