package com.abrazame.service_gestion.controller;

import com.abrazame.service_gestion.model.Donacion;
import com.abrazame.service_gestion.service.DonacionService;
import com.abrazame.service_gestion.service.EmailNotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gestion")
@CrossOrigin(origins = "*")
public class DonacionController {

    @Autowired private DonacionService donacionService;
    @Autowired private EmailNotificacionService emailService;

    // ═══════════════════════════════════════════════════════════
    //  GET — Consultas
    // ═══════════════════════════════════════════════════════════

    /** GET /gestion  →  lista todas las donaciones
     *  Postman: GET http://localhost:9090/gestion */
    @GetMapping
    public List<Donacion> listar() {
        return donacionService.listarTodas();
    }

    /** GET /gestion/resumen  →  conteo por estado (Pendiente, Aprobado, etc.)
     *  Postman: GET http://localhost:9090/gestion/resumen */
    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return donacionService.resumen();
    }

    /** GET /gestion/estado/{estado}  →  filtrar por estado
     *  Postman: GET http://localhost:9090/gestion/estado/Pendiente
     *  Estados válidos: Pendiente | Prevalidado | Aprobado | Rechazado | Descartado */
    @GetMapping("/estado/{estado}")
    public List<Donacion> porEstado(@PathVariable String estado) {
        return donacionService.listarPorEstado(estado);
    }

    /** GET /gestion/donante/{donanteId}  →  donaciones de un donante específico
     *  Postman: GET http://localhost:9090/gestion/donante/1 */
    @GetMapping("/donante/{donanteId}")
    public List<Donacion> porDonante(@PathVariable Long donanteId) {
        return donacionService.listarPorDonante(donanteId);
    }

    /** GET /gestion/buscar/{donId}  →  buscar por código DON-XXXXXXXX
     *  Postman: GET http://localhost:9090/gestion/buscar/DON-20240601-001 */
    @GetMapping("/buscar/{donId}")
    public ResponseEntity<Donacion> porDonId(@PathVariable String donId) {
        return donacionService.buscarPorDonId(donId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /gestion/ping  →  healthcheck
     *  Postman: GET http://localhost:9090/gestion/ping */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("service-gestion OK");
    }

    // ═══════════════════════════════════════════════════════════
    //  POST — Crear
    // ═══════════════════════════════════════════════════════════

    /** POST /gestion  →  registrar nueva donación (estado inicial: Pendiente)
     *  Postman: POST http://localhost:9090/gestion
     *  Body: { "donanteId":1,"articuloNombre":"Pañales Talla M",
     *           "cantidad":5,"nombreDonante":"Juan","emailDonante":"juan@email.cl",
     *           "puntoEntrega":"Hogar San José" } */
    @PostMapping
    public Donacion crear(@RequestBody Donacion donacion) {
        Donacion d = donacionService.guardar(donacion);
        if (d.getEmailDonante() != null && !d.getEmailDonante().isBlank())
            emailService.enviarDonacionRecibida(
                    d.getNombreDonante(), d.getEmailDonante(), d.getDonId(), d.getArticuloNombre());
        return d;
    }

    /** POST /gestion/{id}/entrega  →  registrar punto y fecha de entrega
     *  Postman: POST http://localhost:9090/gestion/1/entrega
     *  Body: { "punto":"Hogar San José","hora":"10:00","fecha":"2024-06-15" } */
    @PostMapping("/{id}/entrega")
    public Donacion entrega(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return donacionService.actualizarPuntoEntrega(
                id, body.get("punto"), body.get("hora"), body.get("fecha"));
    }

    /** POST /gestion/{id}/upload  →  subir imagen o video de la donación
     *  Postman: POST http://localhost:9090/gestion/1/upload  (form-data: imagen, video) */
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Donacion upload(@PathVariable Long id,
                           @RequestParam(required = false) MultipartFile imagen,
                           @RequestParam(required = false) MultipartFile video) throws IOException {
        Donacion d = donacionService.buscarPorId(id);
        Path dir = Paths.get("uploads");
        Files.createDirectories(dir);
        if (imagen != null && !imagen.isEmpty()) {
            String n = "don_" + id + "_" + imagen.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(imagen.getInputStream(), dir.resolve(n), StandardCopyOption.REPLACE_EXISTING);
            d.setUrlImagen("/uploads/" + n);
        }
        if (video != null && !video.isEmpty()) {
            String n = "don_" + id + "_" + video.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(video.getInputStream(), dir.resolve(n), StandardCopyOption.REPLACE_EXISTING);
            d.setUrlVideo("/uploads/" + n);
        }
        return donacionService.guardarDirecto(d);
    }

    // ═══════════════════════════════════════════════════════════
    //  PUT — Actualizar
    // ═══════════════════════════════════════════════════════════

    /** PUT /gestion/{id}  →  actualizar datos de donación
     *  Postman: PUT http://localhost:9090/gestion/1
     *  Body: { "cantidad":10,"articuloNombre":"Nombre actualizado" } */
    @PutMapping("/{id}")
    public Donacion actualizar(@PathVariable Long id, @RequestBody Donacion donacion) {
        return donacionService.actualizar(id, donacion);
    }

    /** PUT /gestion/{id}/aprobar  →  aprobar donación (alias PUT de PATCH)
     *  Postman: PUT http://localhost:9090/gestion/1/aprobar */
    @PutMapping("/{id}/aprobar")
    public Donacion aprobarPut(@PathVariable Long id) {
        Donacion d = donacionService.cambiarEstado(id, "Aprobado");
        if (d.getEmailDonante() != null && !d.getEmailDonante().isBlank())
            emailService.enviarDonacionAprobada(
                    d.getNombreDonante(), d.getEmailDonante(), d.getDonId(), d.getArticuloNombre());
        return d;
    }

    /** PUT /gestion/{id}/rechazar  →  rechazar donación (alias PUT de PATCH)
     *  Postman: PUT http://localhost:9090/gestion/1/rechazar
     *  Body (opcional): { "razon":"No cumple criterios" } */
    @PutMapping("/{id}/rechazar")
    public Donacion rechazarPut(@PathVariable Long id,
                                @RequestBody(required = false) Map<String, String> body) {
        String razon = body != null ? body.get("razon") : null;
        Donacion d = donacionService.cambiarEstadoConComentario(id, "Rechazado", razon);
        if (d.getEmailDonante() != null && !d.getEmailDonante().isBlank())
            emailService.enviarDonacionRechazada(
                    d.getNombreDonante(), d.getEmailDonante(), d.getDonId(), d.getArticuloNombre(), razon);
        return d;
    }

    /** PUT /gestion/{id}/prevalidar  →  marcar como prevalidado
     *  Postman: PUT http://localhost:9090/gestion/1/prevalidar
     *  Body (opcional): { "comentario":"Revisar cantidad" } */
    @PutMapping("/{id}/prevalidar")
    public Donacion prevalidarPut(@PathVariable Long id,
                                  @RequestBody(required = false) Map<String, String> body) {
        String com = body != null ? body.get("comentario") : null;
        return donacionService.cambiarEstadoConComentario(id, "Prevalidado", com);
    }

    /** PUT /gestion/{id}/descartar  →  descartar donación
     *  Postman: PUT http://localhost:9090/gestion/1/descartar
     *  Body (opcional): { "comentario":"Artículo no requerido" } */
    @PutMapping("/{id}/descartar")
    public Donacion descartarPut(@PathVariable Long id,
                                 @RequestBody(required = false) Map<String, String> body) {
        String com = body != null ? body.get("comentario") : null;
        return donacionService.cambiarEstadoConComentario(id, "Descartado", com);
    }

    // ═══════════════════════════════════════════════════════════
    //  PATCH — originales conservados
    // ═══════════════════════════════════════════════════════════

    @PatchMapping("/{id}/aprobar")
    public Donacion aprobar(@PathVariable Long id) {
        Donacion d = donacionService.cambiarEstado(id, "Aprobado");
        if (d.getEmailDonante() != null && !d.getEmailDonante().isBlank())
            emailService.enviarDonacionAprobada(
                    d.getNombreDonante(), d.getEmailDonante(), d.getDonId(), d.getArticuloNombre());
        return d;
    }

    @PatchMapping("/{id}/rechazar")
    public Donacion rechazar(@PathVariable Long id,
                             @RequestBody(required = false) Map<String, String> body) {
        String razon = body != null ? body.get("razon") : null;
        Donacion d = donacionService.cambiarEstadoConComentario(id, "Rechazado", razon);
        if (d.getEmailDonante() != null && !d.getEmailDonante().isBlank())
            emailService.enviarDonacionRechazada(
                    d.getNombreDonante(), d.getEmailDonante(), d.getDonId(), d.getArticuloNombre(), razon);
        return d;
    }

    @PatchMapping("/{id}/prevalidar")
    public Donacion prevalidar(@PathVariable Long id,
                               @RequestBody(required = false) Map<String, String> body) {
        String com = body != null ? body.get("comentario") : null;
        return donacionService.cambiarEstadoConComentario(id, "Prevalidado", com);
    }

    @PatchMapping("/{id}/descartar")
    public Donacion descartar(@PathVariable Long id,
                              @RequestBody(required = false) Map<String, String> body) {
        String com = body != null ? body.get("comentario") : null;
        return donacionService.cambiarEstadoConComentario(id, "Descartado", com);
    }

    /**
     * DELETE /gestion/{id}  →  eliminar donación y reflejar en BD
     * Postman: DELETE http://localhost:9090/gestion/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        donacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
