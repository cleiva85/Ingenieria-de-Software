package com.abrazame.service.donante.controller;

import com.abrazame.service.donante.model.Voluntario;
import com.abrazame.service.donante.service.VoluntarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/voluntarios")
@CrossOrigin(origins = "*")
public class VoluntarioController {

    @Autowired private VoluntarioService voluntarioService;
    private static final String UPLOAD_DIR = "uploads/voluntarios/";

    // ═══════════════════════════════════════════════════════════
    //  GET — Consultas
    // ═══════════════════════════════════════════════════════════

    /** GET /voluntarios  →  lista todos los voluntarios
     *  Postman: GET http://localhost:9090/voluntarios */
    @GetMapping
    public List<Voluntario> listar() {
        return voluntarioService.listarTodos();
    }

    /** GET /voluntarios/{id}  →  un voluntario por ID
     *  Postman: GET http://localhost:9090/voluntarios/1 */
    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> obtener(@PathVariable Long id) {
        return voluntarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /voluntarios/correo/{correo}  →  buscar por correo electrónico
     *  Postman: GET http://localhost:9090/voluntarios/correo/maria@test.cl */
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Voluntario> porCorreo(@PathVariable String correo) {
        return voluntarioService.buscarPorCorreo(correo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /voluntarios/archivo/{nombre}  →  descargar archivo CV o certificado
     *  Postman: GET http://localhost:9090/voluntarios/archivo/vol_1_cv_archivo.pdf */
    @GetMapping("/archivo/{nombre}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable String nombre) throws IOException {
        File file = new File(UPLOAD_DIR + nombre);
        if (!file.exists()) return ResponseEntity.notFound().build();
        byte[] bytes = Files.readAllBytes(file.toPath());
        String n = nombre.toLowerCase();
        String ct = n.endsWith(".pdf") ? "application/pdf"
                  : n.endsWith(".docx") ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                  : n.endsWith(".jpg") || n.endsWith(".jpeg") ? "image/jpeg"
                  : n.endsWith(".png") ? "image/png" : "application/octet-stream";
        return ResponseEntity.ok()
                .header("Content-Type", ct)
                .header("Content-Disposition", "inline; filename=\"" + nombre + "\"")
                .body(bytes);
    }

    /** GET /voluntarios/ping  →  healthcheck
     *  Postman: GET http://localhost:9090/voluntarios/ping */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("service-voluntarios OK");
    }

    // ═══════════════════════════════════════════════════════════
    //  POST — Crear
    // ═══════════════════════════════════════════════════════════

    /** POST /voluntarios  →  registrar nuevo voluntario
     *  Postman: POST http://localhost:9090/voluntarios
     *  Body: { "nombre":"Ana","apellido":"López","rut":"14.567.890-1",
     *           "correo":"ana@email.cl","telefono":"+56911111111" } */
    @PostMapping
    public ResponseEntity<Voluntario> crear(@RequestBody Voluntario voluntario) {
        return ResponseEntity.ok(voluntarioService.guardar(voluntario));
    }

    /** POST /voluntarios/{id}/upload  →  subir CV y/o certificado de antecedentes
     *  Postman: POST http://localhost:9090/voluntarios/1/upload  (form-data: cv, certificado) */
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Voluntario> uploadArchivos(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile cv,
            @RequestParam(required = false) MultipartFile certificado) throws IOException {

        Voluntario v = voluntarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado"));

        Path dir = Paths.get(UPLOAD_DIR);
        Files.createDirectories(dir);

        if (cv != null && !cv.isEmpty()) {
            String nombre = "vol_" + id + "_cv_" + cv.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(cv.getInputStream(), dir.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            if (v.getPerfilVoluntario() != null) v.getPerfilVoluntario().setUrlCv(nombre);
        }
        if (certificado != null && !certificado.isEmpty()) {
            String nombre = "vol_" + id + "_cert_" + certificado.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(certificado.getInputStream(), dir.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            if (v.getPerfilVoluntario() != null) v.getPerfilVoluntario().setUrlCertificadoAntecedentes(nombre);
        }
        return ResponseEntity.ok(voluntarioService.guardarDirecto(v));
    }

    // ═══════════════════════════════════════════════════════════
    //  PUT — Actualizar
    // ═══════════════════════════════════════════════════════════

    /** PUT /voluntarios/{id}  →  actualizar datos de voluntario
     *  Postman: PUT http://localhost:9090/voluntarios/1
     *  Body: { "nombre":"Ana Actualizada","telefono":"+56922222222" } */
    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> actualizar(
            @PathVariable Long id,
            @RequestBody Voluntario voluntario) {
        return ResponseEntity.ok(voluntarioService.actualizar(id, voluntario));
    }

    // ═══════════════════════════════════════════════════════════
    //  DELETE — original conservado
    // ═══════════════════════════════════════════════════════════

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        voluntarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
