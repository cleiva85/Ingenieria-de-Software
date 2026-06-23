package com.abrazame.service_catalogo.repository;
import com.abrazame.service_catalogo.model.ArticuloCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ArticuloCatalogoRepository extends JpaRepository<ArticuloCatalogo, Long> {
    List<ArticuloCatalogo> findByActivoTrue();
}
