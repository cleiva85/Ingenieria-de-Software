package com.abrazame.service.donante.controller;

import com.abrazame.service.donante.model.Voluntario;
import com.abrazame.service.donante.service.VoluntarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voluntarios")
public class VoluntarioController {

    @Autowired
    private VoluntarioService voluntarioService;

    @GetMapping
    public List<Voluntario> listar() {
        return voluntarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> obtener(@PathVariable Long id) {
        return voluntarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /voluntarios  ← llamado desde el formulario HTML de voluntarios
    @PostMapping
    public ResponseEntity<Voluntario> crear(@RequestBody Voluntario voluntario) {
        return ResponseEntity.ok(voluntarioService.guardar(voluntario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> actualizar(@PathVariable Long id, @RequestBody Voluntario voluntario) {
        return ResponseEntity.ok(voluntarioService.actualizar(id, voluntario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        voluntarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
