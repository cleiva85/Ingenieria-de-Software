package com.abrazame.service_gestion.controller;
import com.abrazame.service_gestion.model.Donacion;
import com.abrazame.service_gestion.service.DonacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gestion")
public class DonacionController {

    @Autowired private DonacionService donacionService;

    @GetMapping public List<Donacion> listar() { return donacionService.listarTodas(); }
    @PostMapping public Donacion crear(@RequestBody Donacion donacion) { return donacionService.guardar(donacion); }
    @GetMapping("/resumen") public Map<String, Long> resumen() { return donacionService.resumen(); }
    @GetMapping("/estado/{estado}") public List<Donacion> porEstado(@PathVariable String estado) {
        return donacionService.listarPorEstado(estado);
    }
    @GetMapping("/donante/{donanteId}") public List<Donacion> porDonante(@PathVariable Long donanteId) {
        return donacionService.listarPorDonante(donanteId);
    }
    @GetMapping("/buscar/{donId}") public ResponseEntity<Donacion> porDonId(@PathVariable String donId) {
        return donacionService.buscarPorDonId(donId)
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PatchMapping("/{id}/aprobar") public Donacion aprobar(@PathVariable Long id) {
        return donacionService.cambiarEstado(id, "Aprobado");
    }
    @PatchMapping("/{id}/rechazar") public Donacion rechazar(@PathVariable Long id) {
        return donacionService.cambiarEstado(id, "Rechazado");
    }
    // Changed to POST body instead of query params to avoid CORS issues
    @PostMapping("/{id}/entrega") public Donacion actualizarEntrega(
        @PathVariable Long id,
        @RequestBody Map<String, String> body) {
        return donacionService.actualizarPuntoEntrega(
            id,
            body.get("punto"),
            body.get("hora"),
            body.get("fecha")
        );
    }
    @PutMapping("/{id}") public Donacion actualizar(@PathVariable Long id, @RequestBody Donacion donacion) {
        return donacionService.actualizar(id, donacion);
    }
}
