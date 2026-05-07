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
public class DonanteController {

    @Autowired
    private DonantePersonaNaturalService personaNaturalService;

    @Autowired
    private DonanteEmpresaService empresaService;

    // ── Persona Natural ──────────────────────────────────────────────────────

    @GetMapping("/persona-natural")
    public List<DonantePersonaNatural> listarPersonasNaturales() {
        return personaNaturalService.listarTodos();
    }

    @GetMapping("/persona-natural/{id}")
    public ResponseEntity<DonantePersonaNatural> obtenerPersonaNatural(@PathVariable Long id) {
        return personaNaturalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /donantes/persona-natural  ← formulario HTML (Persona Natural)
    @PostMapping("/persona-natural")
    public ResponseEntity<DonantePersonaNatural> crearPersonaNatural(@RequestBody DonantePersonaNatural donante) {
        return ResponseEntity.ok(personaNaturalService.guardar(donante));
    }

    @DeleteMapping("/persona-natural/{id}")
    public ResponseEntity<Void> eliminarPersonaNatural(@PathVariable Long id) {
        personaNaturalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ── Empresa ──────────────────────────────────────────────────────────────

    @GetMapping("/empresa")
    public List<DonanteEmpresa> listarEmpresas() {
        return empresaService.listarTodos();
    }

    @GetMapping("/empresa/{id}")
    public ResponseEntity<DonanteEmpresa> obtenerEmpresa(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /donantes/empresa  ← formulario HTML (Empresa)
    @PostMapping("/empresa")
    public ResponseEntity<DonanteEmpresa> crearEmpresa(@RequestBody DonanteEmpresa empresa) {
        return ResponseEntity.ok(empresaService.guardar(empresa));
    }

    @DeleteMapping("/empresa/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
