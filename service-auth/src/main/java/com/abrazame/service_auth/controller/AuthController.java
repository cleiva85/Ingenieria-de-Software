package com.abrazame.service_auth.controller;

import com.abrazame.service_auth.dto.AuthResponse;
import com.abrazame.service_auth.dto.LoginRequest;
import com.abrazame.service_auth.dto.RegisterRequest;
import com.abrazame.service_auth.dto.SetPasswordRequest;
import com.abrazame.service_auth.model.Usuario;
import com.abrazame.service_auth.service.AuthService;
import com.abrazame.service_auth.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private EmailService emailService;

    // ═══════════════════════════════════════════════════════════
    //  GET — Consultas
    // ═══════════════════════════════════════════════════════════

    /** GET /auth/ping  →  healthcheck
     *  Postman: GET http://localhost:9090/auth/ping */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("service-auth OK");
    }

    /** GET /auth/usuarios  →  lista todos los usuarios
     *  Postman: GET http://localhost:9090/auth/usuarios */
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return authService.listarTodos();
    }

    /** GET /auth/usuarios/{id}  →  un usuario por ID
     *  Postman: GET http://localhost:9090/auth/usuarios/1 */
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return authService.listarTodos().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /auth/voluntarios/activos  →  voluntarios aprobados con contraseña
     *  Postman: GET http://localhost:9090/auth/voluntarios/activos */
    @GetMapping("/voluntarios/activos")
    public List<Usuario> voluntariosActivos() {
        return authService.voluntariosActivos();
    }

    /** GET /auth/voluntarios/pendientes  →  voluntarios sin activar
     *  Postman: GET http://localhost:9090/auth/voluntarios/pendientes */
    @GetMapping("/voluntarios/pendientes")
    public List<Usuario> voluntariosPendientes() {
        return authService.voluntariosPendientes();
    }

    /** GET /auth/activar/verificar?token=xxx  →  valida token de activación
     *  Postman: GET http://localhost:9090/auth/activar/verificar?token=abc123 */
    @GetMapping("/activar/verificar")
    public ResponseEntity<AuthResponse> verificarToken(@RequestParam String token) {
        AuthResponse r = authService.verificarToken(token);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    // ═══════════════════════════════════════════════════════════
    //  POST — Crear / Autenticar
    // ═══════════════════════════════════════════════════════════

    /** POST /auth/register  →  registrar nuevo usuario
     *  Postman: POST http://localhost:9090/auth/register
     *  Body: { "nombre":"María","apellidoPaterno":"Soto","rut":"15.432.765-3",
     *           "email":"maria@test.cl","password":"Clave123!","rol":"Donante" } */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        try {
            AuthResponse r = authService.registrar(req);
            return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
        } catch (Exception e) {
            System.err.println("Error en /auth/register: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                new AuthResponse(false, "Error al registrar: " + e.getMessage())
            );
        }
    }

    /** POST /auth/login  →  iniciar sesión
     *  Postman: POST http://localhost:9090/auth/login
     *  Body: { "email":"maria@test.cl","password":"Clave123!" } */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse r = authService.login(req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.status(401).body(r);
    }

    /** POST /auth/activar/password  →  crear contraseña con token de activación
     *  Postman: POST http://localhost:9090/auth/activar/password
     *  Body: { "token":"uuid-aqui","password":"NuevaClave1!" } */
    @PostMapping("/activar/password")
    public ResponseEntity<AuthResponse> crearContrasena(@Valid @RequestBody SetPasswordRequest req) {
        AuthResponse r = authService.crearContrasena(req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    // ═══════════════════════════════════════════════════════════
    //  PUT — Actualizar
    // ═══════════════════════════════════════════════════════════

    /** PUT /auth/usuarios/{id}  →  actualizar datos de usuario
     *  Postman: PUT http://localhost:9090/auth/usuarios/1
     *  Body: { "nombre":"María Editada","email":"nueva@email.cl","rol":"Donante" } */
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<AuthResponse> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody RegisterRequest req) {
        AuthResponse r = authService.actualizarUsuario(id, req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    /** PUT /auth/voluntarios/{id}/aprobar  →  aprobar voluntario (alias PUT de PATCH)
     *  Postman: PUT http://localhost:9090/auth/voluntarios/3/aprobar */
    @PutMapping("/voluntarios/{id}/aprobar")
    public ResponseEntity<AuthResponse> aprobarVoluntarioPut(@PathVariable Long id) {
        AuthResponse r = authService.activarVoluntario(id);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    /** PUT /auth/voluntarios/{id}/rechazar  →  rechazar voluntario (alias PUT de DELETE)
     *  Postman: PUT http://localhost:9090/auth/voluntarios/3/rechazar */
    @PutMapping("/voluntarios/{id}/rechazar")
    public ResponseEntity<AuthResponse> rechazarVoluntarioPut(@PathVariable Long id) {
        AuthResponse r = authService.rechazarVoluntario(id);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    // ═══════════════════════════════════════════════════════════
    //  PATCH / DELETE — originales conservados
    // ═══════════════════════════════════════════════════════════

    @PatchMapping("/voluntarios/{id}/aprobar")
    public ResponseEntity<AuthResponse> aprobarVoluntario(@PathVariable Long id) {
        AuthResponse r = authService.activarVoluntario(id);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    @DeleteMapping("/voluntarios/{id}/rechazar")
    public ResponseEntity<AuthResponse> rechazarVoluntario(@PathVariable Long id) {
        AuthResponse r = authService.rechazarVoluntario(id);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<AuthResponse> eliminarUsuario(@PathVariable Long id) {
        AuthResponse r = authService.eliminarUsuario(id);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    /**
     * POST /auth/contacto
     * Recibe el formulario de contacto web y reenvía el mensaje por correo
     * a proyectoabrazame1@gmail.com, sin abrir cliente de correo en el navegador.
     * Body JSON: { "nombre": "...", "email": "...", "asunto": "...", "mensaje": "..." }
     */
    @PostMapping("/contacto")
    public ResponseEntity<AuthResponse> recibirContacto(@RequestBody java.util.Map<String, String> body) {
        String nombre  = body.getOrDefault("nombre", "Sin nombre");
        String email   = body.getOrDefault("email", "sin-correo@contacto.cl");
        String asunto  = body.getOrDefault("asunto", "Consulta desde la web");
        String mensaje = body.getOrDefault("mensaje", "");
        if (mensaje.isBlank())
            return ResponseEntity.badRequest().body(new AuthResponse(false, "El mensaje no puede estar vacío."));
        emailService.enviarMensajeContacto(nombre, email, asunto, mensaje);
        return ResponseEntity.ok(new AuthResponse(true, "Mensaje enviado correctamente. Te responderemos a la brevedad."));
    }

    /** Captura errores de validación @Valid y los devuelve como JSON legible */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .findFirst().orElse("Error de validación");
        System.err.println("Validation error en auth: " + msg);
        return ResponseEntity.badRequest().body(new AuthResponse(false, "Error de validación: " + msg));
    }
}
