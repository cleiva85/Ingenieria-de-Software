package com.abrazame.service_auth.config;

import com.abrazame.service_auth.model.Usuario;
import com.abrazame.service_auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UsuarioRepository repo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearSiNoExiste("Ana",      "Soto",    "Rojas",    "admin@abrazame.org",    "Admin1234!", Usuario.Rol.Admin);
        crearSiNoExiste("Luis",     "Ramírez", "Vega",     "director@abrazame.org", "Admin1234!", Usuario.Rol.Director);
        crearSiNoExiste("Sofía",    "Torres",  "Castillo", "super@abrazame.org",    "Admin1234!", Usuario.Rol.SuperAdmin);
        crearSiNoExiste("Valentina","Morales", "Fuentes",  "valentina.morales@gmail.com", "User1234!", Usuario.Rol.Donante);
        crearSiNoExiste("Rodrigo",  "Castro",  "Pizarro",  "rodrigo.castro@outlook.com",  "User1234!", Usuario.Rol.Donante);
    }

    private void crearSiNoExiste(String nombre, String apellidoP, String apellidoM,
                                  String email, String password, Usuario.Rol rol) {
        if (!repo.existsByEmail(email)) {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setApellidoP(apellidoP);
            u.setApellidoM(apellidoM);
            u.setEmail(email);
            u.setPasswordHash(passwordEncoder.encode(password));
            u.setRol(rol);
            u.setActivo(true);
            repo.save(u);
            System.out.println("✅ Usuario creado: " + email);
        }
    }
}
