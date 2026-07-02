package com.abrazame.service_gestion.repository;
import com.abrazame.service_gestion.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
    List<Beneficiario> findByActivoTrue();
    List<Beneficiario> findByTipoBeneficiario(Beneficiario.TipoBeneficiario tipo);
    List<Beneficiario> findByFundacionId(Long fundacionId);
    List<Beneficiario> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
