package com.abrazame.service_catalogo.service;
import com.abrazame.service_catalogo.model.ArticuloCatalogo;
import com.abrazame.service_catalogo.repository.ArticuloCatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class ArticuloCatalogoService {
    @Autowired private ArticuloCatalogoRepository repository;

    public List<ArticuloCatalogo> listarTodos() { return repository.findAll(); }
    public List<ArticuloCatalogo> listarActivos() { return repository.findByActivoTrue(); }
    public Optional<ArticuloCatalogo> buscarPorId(Long id) { return repository.findById(id); }

    public ArticuloCatalogo guardar(ArticuloCatalogo a) {
        if (a.getActivo() == null) a.setActivo(true);
        if (a.getStockActual() == null) a.setStockActual(0);
        return repository.save(a);
    }

    public ArticuloCatalogo toggleActivo(Long id) {
        ArticuloCatalogo a = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        a.setActivo(!a.getActivo());
        return repository.save(a);
    }

    public ArticuloCatalogo incrementarStock(Long id) {
        return incrementarStockCantidad(id, 1);
    }

    public ArticuloCatalogo incrementarStockCantidad(Long id, int cantidad) {
        ArticuloCatalogo a = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        a.setStockActual(a.getStockActual() + cantidad);
        if (a.getMetaStock() != null && a.getStockActual() >= a.getMetaStock()) a.setActivo(false);
        return repository.save(a);
    }

    /** Resta stock al confirmar entrega a un beneficiario. Nunca baja de 0. */
    public ArticuloCatalogo decrementarStockCantidad(Long id, int cantidad) {
        ArticuloCatalogo a = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        int nuevoStock = a.getStockActual() - cantidad;
        a.setStockActual(Math.max(nuevoStock, 0));
        // Si vuelve a quedar bajo la meta, se reactiva en el portal público
        if (a.getMetaStock() != null && a.getStockActual() < a.getMetaStock()) a.setActivo(true);
        return repository.save(a);
    }

    public ArticuloCatalogo actualizar(Long id, ArticuloCatalogo datos) {
        ArticuloCatalogo a = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        a.setNombre(datos.getNombre()); a.setCategoria(datos.getCategoria());
        a.setRangoEdad(datos.getRangoEdad()); a.setPrioridad(datos.getPrioridad());
        a.setMetaStock(datos.getMetaStock()); a.setStockActual(datos.getStockActual());
        a.setActivo(datos.getActivo()); a.setDescripcion(datos.getDescripcion());
        return repository.save(a);
    }

    public void eliminar(Long id) { repository.deleteById(id); }
}
