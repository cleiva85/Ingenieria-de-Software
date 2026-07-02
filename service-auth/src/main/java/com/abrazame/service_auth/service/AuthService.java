package com.abrazame.service_auth.service;

import com.abrazame.service_auth.dto.AuthResponse;
import com.abrazame.service_auth.dto.LoginRequest;
import com.abrazame.service_auth.dto.RegisterRequest;
import com.abrazame.service_auth.dto.SetPasswordRequest;
import com.abrazame.service_auth.model.Usuario;
import com.abrazame.service_auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 15;

    private static final Map<Usuario.Rol, String> REDIRECT_MAP = Map.of(
        Usuario.Rol.Donante,     "catalogo.html",
        Usuario.Rol.Voluntario,  "vista_voluntario.html",
        Usuario.Rol.Admin,       "panel_admin.html",
        Usuario.Rol.Director,    "vista_director.html",
        Usuario.Rol.SuperAdmin,  "vista_superadmin.html"
    );

    @Autowired private UsuarioRepository repo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    // ── REGISTRO ─────────────────────────────────────────────────────
    public AuthResponse registrar(RegisterRequest req) {
        // ── Caso 1: correo ya existe ─────────────────────────────────
        if (repo.existsByEmail(req.getEmail())) {
            java.util.Optional<Usuario> existente = repo.findByEmail(req.getEmail());
            if (existente.isPresent()) {
                Usuario ue = existente.get();
                // Permitir re-registro solo si es Voluntario pendiente (sin contraseña, inactivo)
                if (ue.getRol() == Usuario.Rol.Voluntario && !ue.isActivo() && ue.getPasswordHash() == null) {
                    ue.setNombre(req.getNombre());
                    ue.setNombre2(req.getNombre2());
                    ue.setApellidoP(req.getApellidoP() != null ? req.getApellidoP() : "-");
                    ue.setApellidoM(req.getApellidoM() != null ? req.getApellidoM() : "-");
                    // Solo actualizar RUT si no hay colisión con otro usuario
                    if (req.getRut() != null && !req.getRut().isBlank()) {
                        java.util.Optional<Usuario> rutDupe = repo.findByRut(req.getRut());
                        if (!rutDupe.isPresent() || rutDupe.get().getId().equals(ue.getId())) {
                            ue.setRut(req.getRut());
                        }
                    }
                    if (req.getTelefono() != null) ue.setTelefono(req.getTelefono());
                    if (req.getFechaNacimiento() != null && !req.getFechaNacimiento().isBlank()) {
                        try { ue.setFechaNacimiento(LocalDate.parse(req.getFechaNacimiento())); } catch(Exception ignore) {}
                    }
                    ue.setFechaRegistro(LocalDateTime.now());
                    repo.save(ue);
                    emailService.enviarSolicitudRecibida(ue.getNombre(), ue.getEmail());
                    return new AuthResponse(true, "Solicitud reenviada. Recibirás un correo cuando tu cuenta sea activada.");
                }
            }
            return new AuthResponse(false, "El correo ya está registrado.");
        }

        // ── Caso 2: RUT ya existe en otro usuario ────────────────────
        if (req.getRut() != null && !req.getRut().isBlank()) {
            java.util.Optional<Usuario> rutExistente = repo.findByRut(req.getRut());
            if (rutExistente.isPresent()) {
                Usuario ue = rutExistente.get();
                // Si es voluntario pendiente con distinto correo, actualizar correo y reenviar
                if (ue.getRol() == Usuario.Rol.Voluntario && !ue.isActivo() && ue.getPasswordHash() == null) {
                    ue.setEmail(req.getEmail());
                    ue.setNombre(req.getNombre());
                    ue.setNombre2(req.getNombre2());
                    ue.setApellidoP(req.getApellidoP() != null ? req.getApellidoP() : "-");
                    ue.setApellidoM(req.getApellidoM() != null ? req.getApellidoM() : "-");
                    if (req.getTelefono() != null) ue.setTelefono(req.getTelefono());
                    ue.setFechaRegistro(LocalDateTime.now());
                    repo.save(ue);
                    emailService.enviarSolicitudRecibida(ue.getNombre(), ue.getEmail());
                    return new AuthResponse(true, "Solicitud enviada. Recibirás un correo cuando tu cuenta sea activada.");
                }
                return new AuthResponse(false, "El RUT ya está registrado.");
            }
        }

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setNombre2(req.getNombre2());
        u.setApellidoP(req.getApellidoP() != null ? req.getApellidoP() : "-");
        u.setApellidoM(req.getApellidoM() != null ? req.getApellidoM() : "-");
        u.setRut(req.getRut());
        u.setTelefono(req.getTelefono());
        u.setEmail(req.getEmail());
        u.setRol(req.getRol());
        u.setFechaRegistro(LocalDateTime.now());

        if (req.getFechaNacimiento() != null && !req.getFechaNacimiento().isBlank())
            u.setFechaNacimiento(LocalDate.parse(req.getFechaNacimiento()));

        if (req.getRol() == Usuario.Rol.Voluntario) {
            // Voluntario queda inactivo hasta que admin lo apruebe
            u.setActivo(false);
            u.setPasswordHash(null); // sin contraseña aún
            repo.save(u);
            // Enviar correo de solicitud recibida
            System.out.println("📧 Enviando correo de solicitud a: " + u.getEmail());
            emailService.enviarSolicitudRecibida(u.getNombre(), u.getEmail());
            System.out.println("📧 Correo enviado correctamente");
            return new AuthResponse(true, "Solicitud enviada. Recibirás un correo cuando tu cuenta sea activada.");
        } else {
            // Donante → activo de inmediato con contraseña
            if (req.getPassword() == null || req.getPassword().isBlank())
                return new AuthResponse(false, "La contraseña es obligatoria.");
            u.setActivo(true);
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            repo.save(u);
            return new AuthResponse(true, "Cuenta creada exitosamente. Ya puedes iniciar sesión.");
        }
    }

    // ── ACTIVAR VOLUNTARIO (admin aprueba) ────────────────────────────
    public AuthResponse activarVoluntario(Long id) {
        Optional<Usuario> opt = repo.findById(id);
        if (opt.isEmpty()) return new AuthResponse(false, "Usuario no encontrado.");
        Usuario u = opt.get();
        if (u.getRol() != Usuario.Rol.Voluntario)
            return new AuthResponse(false, "El usuario no es voluntario.");

        // Generar token único de activación (expira en 24h)
        String token = UUID.randomUUID().toString();
        u.setTokenActivacion(token);
        u.setTokenExpira(LocalDateTime.now().plusHours(24));
        repo.save(u);

        // Enviar correo con link de activación
        emailService.enviarAprobacion(u.getNombre(), u.getEmail(), token);
        return new AuthResponse(true, "Voluntario aprobado. Se envió el correo con el link de activación.");
    }

    // ── RECHAZAR VOLUNTARIO ───────────────────────────────────────────
    public AuthResponse rechazarVoluntario(Long id) {
        Optional<Usuario> opt = repo.findById(id);
        if (opt.isEmpty()) return new AuthResponse(false, "Usuario no encontrado.");
        Usuario u = opt.get();
        emailService.enviarRechazo(u.getNombre(), u.getEmail());
        repo.delete(u);
        return new AuthResponse(true, "Voluntario rechazado y eliminado.");
    }

    // ── VERIFICAR TOKEN ───────────────────────────────────────────────
    public AuthResponse verificarToken(String token) {
        Optional<Usuario> opt = repo.findByTokenActivacion(token);
        if (opt.isEmpty()) return new AuthResponse(false, "Token inválido o ya usado.");
        Usuario u = opt.get();
        if (u.getTokenExpira() != null && LocalDateTime.now().isAfter(u.getTokenExpira()))
            return new AuthResponse(false, "El enlace ha expirado. Contacta al administrador.");
        return new AuthResponse(true, "Token válido.", null, u.getNombre(), null);
    }

    // ── CREAR CONTRASEÑA (voluntario aprobado) ────────────────────────
    public AuthResponse crearContrasena(SetPasswordRequest req) {
        Optional<Usuario> opt = repo.findByTokenActivacion(req.getToken());
        if (opt.isEmpty()) return new AuthResponse(false, "Token inválido o ya usado.");
        Usuario u = opt.get();
        if (u.getTokenExpira() != null && LocalDateTime.now().isAfter(u.getTokenExpira()))
            return new AuthResponse(false, "El enlace ha expirado. Contacta al administrador.");

        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setActivo(true);
        u.setTokenActivacion(null);
        u.setTokenExpira(null);
        repo.save(u);
        return new AuthResponse(true, "¡Contraseña creada! Ya puedes iniciar sesión.", "Voluntario", u.getNombre(), "login_voluntario.html");
    }

    // ── LOGIN ─────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        Optional<Usuario> opt = repo.findByEmail(req.getEmail());
        if (opt.isEmpty()) return new AuthResponse(false, "Correo o contraseña incorrectos.");

        Usuario u = opt.get();

        if (u.getBloqueadoHasta() != null && LocalDateTime.now().isBefore(u.getBloqueadoHasta())) {
            long min = java.time.Duration.between(LocalDateTime.now(), u.getBloqueadoHasta()).toMinutes() + 1;
            return new AuthResponse(false, "Cuenta bloqueada. Intenta en " + min + " minuto(s).");
        }

        if (!u.isActivo()) {
            if (u.getRol() == Usuario.Rol.Voluntario && u.getPasswordHash() == null)
                return new AuthResponse(false, "Tu solicitud aún está siendo revisada. Te notificaremos por correo cuando sea aprobada.");
            return new AuthResponse(false, "Tu cuenta aún no ha sido activada.");
        }

        if (!u.getRol().equals(req.getRol()))
            return new AuthResponse(false, "El rol seleccionado no corresponde a tu cuenta.");

        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            int intentos = u.getIntentosFallidos() + 1;
            u.setIntentosFallidos(intentos);
            if (intentos >= MAX_INTENTOS) {
                u.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
                u.setIntentosFallidos(0);
                repo.save(u);
                return new AuthResponse(false, "Demasiados intentos. Cuenta bloqueada por " + MINUTOS_BLOQUEO + " minutos.");
            }
            repo.save(u);
            return new AuthResponse(false, "Contraseña incorrecta. Te quedan " + (MAX_INTENTOS - intentos) + " intento(s).");
        }

        u.setIntentosFallidos(0);
        u.setBloqueadoHasta(null);
        repo.save(u);

        String nombre = u.getNombre() + " " + u.getApellidoP();
        String redirect = REDIRECT_MAP.getOrDefault(u.getRol(), "catalogo.html");
        return new AuthResponse(true, "Bienvenido/a, " + nombre + ".", u.getRol().name(), nombre, redirect);
    }

    // ── LISTAR VOLUNTARIOS PENDIENTES (para admin) ────────────────────
    public java.util.List<Usuario> listarTodos() { return repo.findAll(); }

    public AuthResponse actualizarUsuario(Long id, RegisterRequest req) {
        Optional<Usuario> opt = repo.findById(id);
        if(opt.isEmpty()) return new AuthResponse(false, "Usuario no encontrado.");
        Usuario u = opt.get();
        if(req.getNombre() != null) u.setNombre(req.getNombre());
        if(req.getApellidoP() != null) u.setApellidoP(req.getApellidoP());
        if(req.getApellidoM() != null) u.setApellidoM(req.getApellidoM());
        if(req.getEmail() != null) u.setEmail(req.getEmail());
        if(req.getRol() != null) u.setRol(req.getRol());
        if(req.getRut() != null) u.setRut(req.getRut());
        if(req.getTelefono() != null) u.setTelefono(req.getTelefono());
        if(req.getPassword() != null && !req.getPassword().isBlank())
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        repo.save(u);
        return new AuthResponse(true, "Usuario actualizado.");
    }

    public AuthResponse eliminarUsuario(Long id) {
        if(!repo.existsById(id)) return new AuthResponse(false, "Usuario no encontrado.");
        repo.deleteById(id);
        return new AuthResponse(true, "Usuario eliminado.");
    }

    public java.util.List<Usuario> voluntariosPendientes() {
        return repo.findByRolAndActivo(Usuario.Rol.Voluntario, false);
    }

    public java.util.List<Usuario> voluntariosActivos() {
        return repo.findByRolAndActivo(Usuario.Rol.Voluntario, true);
    }
}
