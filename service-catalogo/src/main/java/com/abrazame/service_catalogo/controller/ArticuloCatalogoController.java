package com.abrazame.service_catalogo.controller;

import com.abrazame.service_catalogo.model.ArticuloCatalogo;
import com.abrazame.service_catalogo.service.ArticuloCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/catalogo")
@CrossOrigin(origins = "*")
public class ArticuloCatalogoController {

    @Autowired private ArticuloCatalogoService service;

    // ═══════════════════════════════════════════════════════════
    //  GET — Consultas
    // ═══════════════════════════════════════════════════════════

    /** GET /catalogo  →  todos los artículos
     *  Postman: GET http://localhost:9090/catalogo */
    @GetMapping
    public List<ArticuloCatalogo> listar() {
        return service.listarTodos();
    }

    /** GET /catalogo/activos  →  solo artículos activos (visibles en portal)
     *  Postman: GET http://localhost:9090/catalogo/activos */
    @GetMapping("/activos")
    public List<ArticuloCatalogo> listarActivos() {
        return service.listarActivos();
    }

    /** GET /catalogo/{id}  →  un artículo por ID
     *  Postman: GET http://localhost:9090/catalogo/1 */
    @GetMapping("/{id}")
    public ResponseEntity<ArticuloCatalogo> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /catalogo/ping  →  healthcheck
     *  Postman: GET http://localhost:9090/catalogo/ping */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("service-catalogo OK");
    }

    // ═══════════════════════════════════════════════════════════
    //  POST — Crear
    // ═══════════════════════════════════════════════════════════

    /** POST /catalogo  →  crear nuevo artículo
     *  Postman: POST http://localhost:9090/catalogo
     *  Body: { "nombre":"Pañales Talla M","descripcion":"Para bebés",
     *           "prioridad":"ALTA","rangoEdad":"0-2","metaStock":100,
     *           "stockActual":0,"activo":true } */
    @PostMapping
    public ArticuloCatalogo crear(@RequestBody ArticuloCatalogo a) {
        return service.guardar(a);
    }

    // ═══════════════════════════════════════════════════════════
    //  PUT — Actualizar
    // ═══════════════════════════════════════════════════════════

    /** PUT /catalogo/{id}  →  actualizar artículo completo
     *  Postman: PUT http://localhost:9090/catalogo/1
     *  Body: { "nombre":"Pañales actualizado","prioridad":"MEDIA","metaStock":150 } */
    @PutMapping("/{id}")
    public ArticuloCatalogo actualizar(@PathVariable Long id, @RequestBody ArticuloCatalogo a) {
        return service.actualizar(id, a);
    }

    /** PUT /catalogo/{id}/toggle  →  activar/desactivar visibilidad (alias PUT de PATCH)
     *  Postman: PUT http://localhost:9090/catalogo/1/toggle */
    @PutMapping("/{id}/toggle")
    public ArticuloCatalogo toggleActivoPut(@PathVariable Long id) {
        return service.toggleActivo(id);
    }

    /** PUT /catalogo/{id}/incrementar/{cantidad}  →  incrementar stock N unidades
     *  Postman: PUT http://localhost:9090/catalogo/1/incrementar/5 */
    @PutMapping("/{id}/incrementar/{cantidad}")
    public ArticuloCatalogo incrementarStockPut(@PathVariable Long id, @PathVariable int cantidad) {
        return service.incrementarStockCantidad(id, cantidad);
    }

    // ═══════════════════════════════════════════════════════════
    //  PATCH / DELETE — originales conservados
    // ═══════════════════════════════════════════════════════════

    @PatchMapping("/{id}/toggle")
    public ArticuloCatalogo toggleActivo(@PathVariable Long id) {
        return service.toggleActivo(id);
    }

    @PatchMapping("/{id}/incrementar")
    public ArticuloCatalogo incrementarStock(@PathVariable Long id) {
        return service.incrementarStock(id);
    }

    @PatchMapping("/{id}/incrementar/{cantidad}")
    public ArticuloCatalogo incrementarStockCantidad(@PathVariable Long id, @PathVariable int cantidad) {
        return service.incrementarStockCantidad(id, cantidad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
