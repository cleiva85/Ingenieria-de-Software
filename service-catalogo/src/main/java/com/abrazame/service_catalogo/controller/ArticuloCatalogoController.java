package com.abrazame.service_catalogo.controller;
import com.abrazame.service_catalogo.model.ArticuloCatalogo;
import com.abrazame.service_catalogo.service.ArticuloCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/catalogo")
public class ArticuloCatalogoController {
    @Autowired private ArticuloCatalogoService service;

    @GetMapping public List<ArticuloCatalogo> listar() { return service.listarTodos(); }
    @GetMapping("/activos") public List<ArticuloCatalogo> listarActivos() { return service.listarActivos(); }
    @GetMapping("/{id}") public ResponseEntity<ArticuloCatalogo> obtener(@PathVariable Long id) {
        return service.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping public ArticuloCatalogo crear(@RequestBody ArticuloCatalogo a) { return service.guardar(a); }
    @PutMapping("/{id}") public ArticuloCatalogo actualizar(@PathVariable Long id, @RequestBody ArticuloCatalogo a) {
        return service.actualizar(id, a);
    }
    @PatchMapping("/{id}/toggle") public ArticuloCatalogo toggleActivo(@PathVariable Long id) { return service.toggleActivo(id); }
    @PatchMapping("/{id}/incrementar") public ArticuloCatalogo incrementarStock(@PathVariable Long id) { return service.incrementarStock(id); }
    @PatchMapping("/{id}/incrementar/{cantidad}") public ArticuloCatalogo incrementarStockCantidad(@PathVariable Long id, @PathVariable int cantidad) { return service.incrementarStockCantidad(id, cantidad); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
}
