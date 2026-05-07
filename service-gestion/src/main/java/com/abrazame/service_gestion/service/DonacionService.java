package com.abrazame.service_gestion.service;
import com.abrazame.service_gestion.model.Donacion;
import com.abrazame.service_gestion.repository.DonacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DonacionService {

    @Autowired private DonacionRepository donacionRepository;

    public List<Donacion> listarTodas() { return donacionRepository.findAll(); }
    public List<Donacion> listarPorEstado(String estado) { return donacionRepository.findByEstado(estado); }
    public List<Donacion> listarPorDonante(Long donanteId) { return donacionRepository.findByDonanteId(donanteId); }
    public Optional<Donacion> buscarPorDonId(String donId) { return donacionRepository.findByDonId(donId); }

    public Donacion guardar(Donacion donacion) {
        // Auto-generate DON-ID
        long total = donacionRepository.count() + 1;
        donacion.setDonId(String.format("DON-%03d", total));
        return donacionRepository.save(donacion);
    }

    public Donacion cambiarEstado(Long id, String nuevoEstado) {
        Donacion d = donacionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donacion no encontrada"));
        d.setEstado(nuevoEstado);
        return donacionRepository.save(d);
    }

    public Donacion actualizarPuntoEntrega(Long id, String punto, String hora, String fecha) {
        Donacion d = donacionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        d.setPuntoEntrega(punto);
        d.setHoraEstimada(hora);
        if (fecha != null && !fecha.isBlank())
            d.setFechaEntrega(java.time.LocalDate.parse(fecha));
        return donacionRepository.save(d);
    }

    public Donacion actualizar(Long id, Donacion datos) {
    Donacion d = donacionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donacion no encontrada"));
    if (datos.getArticuloNombre() != null) d.setArticuloNombre(datos.getArticuloNombre());
    if (datos.getCantidad() != null) d.setCantidad(datos.getCantidad());
    if (datos.getEstado() != null) d.setEstado(datos.getEstado());
    if (datos.getNombreDonante() != null) d.setNombreDonante(datos.getNombreDonante());
    if (datos.getPuntoEntrega() != null) d.setPuntoEntrega(datos.getPuntoEntrega());
    if (datos.getHoraEstimada() != null) d.setHoraEstimada(datos.getHoraEstimada());
    if (datos.getFechaEntrega() != null) d.setFechaEntrega(datos.getFechaEntrega());
    return donacionRepository.save(d);
  }

  public Map<String, Long> resumen() {
        Map<String, Long> r = new HashMap<>();
        r.put("pendientes", donacionRepository.countByEstado("Pendiente"));
        r.put("aprobados", donacionRepository.countByEstado("Aprobado"));
        r.put("rechazados", donacionRepository.countByEstado("Rechazado"));
        r.put("total", donacionRepository.count());
        return r;
    }
}
