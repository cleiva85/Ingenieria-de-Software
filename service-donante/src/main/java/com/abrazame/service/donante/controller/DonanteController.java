package com.abrazame.service.donante.controller;

import com.abrazame.service.donante.model.DonanteEmpresa;
import com.abrazame.service.donante.model.DonantePersonaNatural;
import com.abrazame.service.donante.service.DonanteEmpresaService;
import com.abrazame.service.donante.service.DonantePersonaNaturalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/donantes")
@CrossOrigin(origins = "*")
public class DonanteController {

    @Autowired private DonantePersonaNaturalService personaNaturalService;
    @Autowired private DonanteEmpresaService empresaService;

    // ═══════════════════════════════════════════════════════════
    //  GET — Persona Natural
    // ═══════════════════════════════════════════════════════════

    /** GET /donantes/persona-natural  →  lista todas las personas naturales
     *  Postman: GET http://localhost:9090/donantes/persona-natural */
    @GetMapping("/persona-natural")
    public List<DonantePersonaNatural> listarPersonasNaturales() {
        return personaNaturalService.listarTodos();
    }

    /** GET /donantes/persona-natural/{id}  →  una persona natural por ID
     *  Postman: GET http://localhost:9090/donantes/persona-natural/1 */
    @GetMapping("/persona-natural/{id}")
    public ResponseEntity<DonantePersonaNatural> obtenerPersonaNatural(@PathVariable Long id) {
        return personaNaturalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ═══════════════════════════════════════════════════════════
    //  GET — Empresa
    // ═══════════════════════════════════════════════════════════

    /** GET /donantes/empresa  →  lista todas las empresas donantes
     *  Postman: GET http://localhost:9090/donantes/empresa */
    @GetMapping("/empresa")
    public List<DonanteEmpresa> listarEmpresas() {
        return empresaService.listarTodos();
    }

    /** GET /donantes/empresa/{id}  →  una empresa por ID
     *  Postman: GET http://localhost:9090/donantes/empresa/1 */
    @GetMapping("/empresa/{id}")
    public ResponseEntity<DonanteEmpresa> obtenerEmpresa(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /donantes/ping  →  healthcheck
     *  Postman: GET http://localhost:9090/donantes/ping */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("service-donante OK");
    }

    // ═══════════════════════════════════════════════════════════
    //  POST — Crear
    // ═══════════════════════════════════════════════════════════

    /** POST /donantes/persona-natural  →  registrar persona natural
     *  Postman: POST http://localhost:9090/donantes/persona-natural
     *  Body: { "nombre":"Juan","apellido":"Pérez","rut":"12.345.678-9",
     *           "email":"juan@email.cl","telefono":"+56912345678" } */
    @PostMapping("/persona-natural")
    public ResponseEntity<DonantePersonaNatural> crearPersonaNatural(
            @RequestBody DonantePersonaNatural donante) {
        return ResponseEntity.ok(personaNaturalService.guardar(donante));
    }

    /** POST /donantes/empresa  →  registrar empresa donante
     *  Postman: POST http://localhost:9090/donantes/empresa
     *  Body: { "razonSocial":"Empresa SA","rut":"76.543.210-K",
     *           "email":"contacto@empresa.cl","telefono":"+56222345678" } */
    @PostMapping("/empresa")
    public ResponseEntity<DonanteEmpresa> crearEmpresa(@RequestBody DonanteEmpresa empresa) {
        return ResponseEntity.ok(empresaService.guardar(empresa));
    }

    // ═══════════════════════════════════════════════════════════
    //  PUT — Actualizar
    // ═══════════════════════════════════════════════════════════

    /** PUT /donantes/persona-natural/{id}  →  actualizar persona natural
     *  Postman: PUT http://localhost:9090/donantes/persona-natural/1
     *  Body: { "nombre":"Juan Actualizado","telefono":"+56987654321" } */
    @PutMapping("/persona-natural/{id}")
    public ResponseEntity<DonantePersonaNatural> actualizarPersonaNatural(
            @PathVariable Long id,
            @RequestBody DonantePersonaNatural donante) {
        return personaNaturalService.buscarPorId(id).map(existing -> {
            donante.setId(id);
            return ResponseEntity.ok(personaNaturalService.guardar(donante));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** PUT /donantes/empresa/{id}  →  actualizar empresa donante
     *  Postman: PUT http://localhost:9090/donantes/empresa/1
     *  Body: { "razonSocial":"Empresa Actualizada SA","email":"nuevo@empresa.cl" } */
    @PutMapping("/empresa/{id}")
    public ResponseEntity<DonanteEmpresa> actualizarEmpresa(
            @PathVariable Long id,
            @RequestBody DonanteEmpresa empresa) {
        return empresaService.buscarPorId(id).map(existing -> {
            empresa.setId(id);
            return ResponseEntity.ok(empresaService.guardar(empresa));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ═══════════════════════════════════════════════════════════
    //  DELETE — originales conservados
    // ═══════════════════════════════════════════════════════════

    @DeleteMapping("/persona-natural/{id}")
    public ResponseEntity<Void> eliminarPersonaNatural(@PathVariable Long id) {
        personaNaturalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empresa/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
