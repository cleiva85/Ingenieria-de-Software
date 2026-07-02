package com.abrazame.service_gestion.controller;

import com.abrazame.service_gestion.dto.AsignarBeneficiarioRequest;
import com.abrazame.service_gestion.dto.ConfirmarEntregaRequest;
import com.abrazame.service_gestion.dto.ResolverTicketRequest;
import com.abrazame.service_gestion.model.Beneficiario;
import com.abrazame.service_gestion.model.Donacion;
import com.abrazame.service_gestion.model.FundacionColaboradora;
import com.abrazame.service_gestion.model.HistorialEntrega;
import com.abrazame.service_gestion.model.TicketAprobacionDirector;
import com.abrazame.service_gestion.repository.BeneficiarioRepository;
import com.abrazame.service_gestion.repository.DonacionRepository;
import com.abrazame.service_gestion.repository.FundacionColaboradoraRepository;
import com.abrazame.service_gestion.repository.HistorialEntregaRepository;
import com.abrazame.service_gestion.repository.TicketAprobacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller v3.0 — Módulo Beneficiarios, Fundaciones Colaboradoras,
 * Tickets de Aprobación e Historial de Entregas con descuento de inventario.
 * Expuesto en http://localhost:8083 (ruteado por Gateway en /gestion/**)
 *
 * Flujo de inventario:
 *  1. Al APROBAR una donación → se SUMA stock en service-catalogo.
 *  2. Al CONFIRMAR ENTREGA a un beneficiario → se RESTA stock en
 *     service-catalogo y se registra en historial_entregas con el
 *     destino exacto (beneficiario + fundación/residencia).
 *
 * Endpoints:
 *  GET  /gestion/fundaciones                     → listar fundaciones/residencias colaboradoras
 *  GET  /gestion/beneficiarios                    → listar beneficiarios activos (con fundación enriquecida)
 *  GET  /gestion/beneficiarios/{id}                → detalle de un beneficiario
 *  POST /gestion/beneficiarios                     → crear beneficiario (Admin/Director)
 *  PUT  /gestion/{donId}/asignar-beneficiario     → Director asigna beneficiario + crea ticket
 *  GET  /gestion/tickets                          → listar todos los tickets
 *  PUT  /gestion/tickets/{ticketId}/resolver      → Director aprueba/rechaza ticket
 *  PUT  /gestion/{donId}/confirmar-entrega        → Voluntario/Admin confirma entrega física (resta stock)
 *  GET  /gestion/historial-entregas               → historial completo de entregas (trazabilidad)
 *  GET  /gestion/historial-entregas/beneficiario/{id} → historial de un beneficiario específico
 */
@RestController
@RequestMapping("/gestion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BeneficiarioController {

    private final BeneficiarioRepository beneficiarioRepo;
    private final FundacionColaboradoraRepository fundacionRepo;
    private final DonacionRepository donacionRepo;
    private final TicketAprobacionRepository ticketRepo;
    private final HistorialEntregaRepository historialRepo;
    private final WebClient catalogoWebClient;

    // ─── FUNDACIONES COLABORADORAS ─────────────────────────────────────

    @GetMapping("/fundaciones")
    public List<FundacionColaboradora> listarFundaciones() {
        return fundacionRepo.findByActivoTrue();
    }

    // ─── BENEFICIARIOS ────────────────────────────────────────────────

    private Beneficiario enriquecer(Beneficiario b) {
        fundacionRepo.findById(b.getFundacionId()).ifPresent(f -> {
            b.setFundacionResidencia(f.getNombre());
            b.setRegion(f.getRegion());
        });
        return b;
    }

    @GetMapping("/beneficiarios")
    public List<Beneficiario> listarBeneficiarios() {
        List<Beneficiario> lista = beneficiarioRepo.findByActivoTrue();
        lista.forEach(this::enriquecer);
        return lista;
    }

    @GetMapping("/beneficiarios/{id}")
    public ResponseEntity<Beneficiario> obtenerBeneficiario(@PathVariable Long id) {
        return beneficiarioRepo.findById(id)
                .map(this::enriquecer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/beneficiarios")
    public ResponseEntity<Beneficiario> crearBeneficiario(@RequestBody Beneficiario beneficiario) {
        return ResponseEntity.ok(enriquecer(beneficiarioRepo.save(beneficiario)));
    }

    @PutMapping("/beneficiarios/{id}")
    public ResponseEntity<Beneficiario> actualizarBeneficiario(
            @PathVariable Long id, @RequestBody Beneficiario datos) {
        return beneficiarioRepo.findById(id).map(b -> {
            b.setNombre(datos.getNombre());
            b.setFundacionId(datos.getFundacionId());
            b.setContactoNombre(datos.getContactoNombre());
            b.setContactoCargo(datos.getContactoCargo());
            b.setContactoTelefono(datos.getContactoTelefono());
            b.setContactoEmail(datos.getContactoEmail());
            b.setDescripcionNecesidad(datos.getDescripcionNecesidad());
            b.setObservaciones(datos.getObservaciones());
            return ResponseEntity.ok(enriquecer(beneficiarioRepo.save(b)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ─── ASIGNACIÓN DIRECTOR ─────────────────────────────────────────

    @PutMapping("/{donId}/asignar-beneficiario")
    public ResponseEntity<?> asignarBeneficiario(
            @PathVariable Long donId,
            @RequestBody AsignarBeneficiarioRequest req) {

        Donacion donacion = donacionRepo.findById(donId).orElse(null);
        if (donacion == null) return ResponseEntity.notFound().build();

        Beneficiario benef = beneficiarioRepo.findById(req.getBeneficiarioId()).orElse(null);
        if (benef == null) return ResponseEntity.badRequest()
                .body(Map.of("error", "Beneficiario no encontrado"));

        donacion.setBeneficiarioId(benef.getId());
        donacion.setBeneficiarioNombre(benef.getNombre());
        donacion.setEstado("Asignado");
        donacionRepo.save(donacion);

        TicketAprobacionDirector ticket = new TicketAprobacionDirector();
        ticket.setDonacionId(donId);
        ticket.setBeneficiarioId(benef.getId());
        ticket.setDirectorUsuarioId(req.getDirectorId());
        ticket.setDirectorNombre(req.getDirectorNombre());
        ticket.setObservacionesDirector(req.getObservaciones());
        ticket.setEstado(TicketAprobacionDirector.EstadoTicket.Pendiente);
        ticketRepo.save(ticket);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Beneficiario asignado y ticket creado",
                "donacion", donacion,
                "ticket", ticket
        ));
    }

    // ─── TICKETS ─────────────────────────────────────────────────────

    @GetMapping("/tickets")
    public List<TicketAprobacionDirector> listarTickets() {
        return ticketRepo.findAll();
    }

    @PutMapping("/tickets/{ticketId}/resolver")
    public ResponseEntity<?> resolverTicket(
            @PathVariable Long ticketId,
            @RequestBody ResolverTicketRequest req) {

        TicketAprobacionDirector ticket = ticketRepo.findById(ticketId).orElse(null);
        if (ticket == null) return ResponseEntity.notFound().build();

        TicketAprobacionDirector.EstadoTicket estado =
                TicketAprobacionDirector.EstadoTicket.valueOf(req.getEstado());

        ticket.setEstado(estado);
        ticket.setFechaDecision(LocalDateTime.now());
        ticket.setMotivoRechazo("Rechazado".equals(req.getEstado()) ? req.getMotivo() : null);
        ticketRepo.save(ticket);

        donacionRepo.findById(ticket.getDonacionId()).ifPresent(don -> {
            if (estado == TicketAprobacionDirector.EstadoTicket.Aprobado) {
                don.setEstado("Aprobado para Entrega");
            } else {
                don.setEstado("Rechazado");
                don.setBeneficiarioId(null);
                don.setBeneficiarioNombre(null);
            }
            donacionRepo.save(don);
        });

        return ResponseEntity.ok(Map.of("mensaje", "Ticket resuelto: " + req.getEstado(), "ticket", ticket));
    }

    // ─── CONFIRMAR ENTREGA (VOLUNTARIO / ADMIN) — descuenta inventario ─

    /**
     * Confirma la entrega física al beneficiario. Esto dispara:
     *  1. Marca la donación como Entregada.
     *  2. Resta del stock del artículo en service-catalogo (si tiene articuloId).
     *  3. Crea un registro permanente en historial_entregas con el destino exacto.
     */
    @PutMapping("/{donId}/confirmar-entrega")
    public ResponseEntity<?> confirmarEntrega(
            @PathVariable Long donId,
            @RequestBody ConfirmarEntregaRequest req) {

        Donacion don = donacionRepo.findById(donId).orElse(null);
        if (don == null) return ResponseEntity.notFound().build();

        if (don.getBeneficiarioId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Esta donación no tiene beneficiario asignado"));
        }

        // 1. Actualizar la donación
        don.setEntregaConfirmada(true);
        don.setFechaConfirmacionEntrega(LocalDateTime.now());
        don.setConfirmadoPor(req.getConfirmadoPor());
        don.setEstado("Entregado");
        donacionRepo.save(don);

        // 2. Descontar stock en el catálogo (si el artículo está vinculado)
        boolean stockDescontado = false;
        if (don.getArticuloId() != null && don.getCantidad() != null) {
            try {
                catalogoWebClient.put()
                        .uri("/catalogo/{id}/decrementar/{cantidad}",
                                don.getArticuloId(), don.getCantidad())
                        .retrieve()
                        .toBodilessEntity()
                        .block();
                stockDescontado = true;
            } catch (Exception e) {
                // No bloquea la confirmación de entrega si catálogo no responde;
                // queda registrado igual en el historial para auditoría manual.
                System.err.println("⚠ No se pudo descontar stock en catálogo: " + e.getMessage());
            }
        }

        // 3. Registrar en historial de entregas (trazabilidad: qué, a quién, dónde)
        Beneficiario benef = beneficiarioRepo.findById(don.getBeneficiarioId()).orElse(null);
        HistorialEntrega historial = new HistorialEntrega();
        historial.setDonacionId(don.getId());
        historial.setDonacionCodigo(don.getDonId());
        historial.setArticuloNombre(don.getArticuloNombre());
        historial.setArticuloId(don.getArticuloId());
        historial.setCantidad(don.getCantidad());
        historial.setBeneficiarioId(don.getBeneficiarioId());
        historial.setBeneficiarioNombre(don.getBeneficiarioNombre());
        if (benef != null) {
            enriquecer(benef);
            historial.setFundacionResidencia(benef.getFundacionResidencia());
            historial.setRegion(benef.getRegion());
        }
        historial.setConfirmadoPor(req.getConfirmadoPor());
        historial.setObservaciones(req.getObservaciones());
        historialRepo.save(historial);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Entrega confirmada exitosamente" +
                        (stockDescontado ? " — stock descontado del inventario" : " — sin vínculo a artículo de catálogo"),
                "donacion", don,
                "historial", historial
        ));
    }

    // ─── HISTORIAL DE ENTREGAS (TRAZABILIDAD) ──────────────────────────

    @GetMapping("/historial-entregas")
    public List<HistorialEntrega> listarHistorial() {
        return historialRepo.findAllByOrderByFechaEntregaDesc();
    }

    @GetMapping("/historial-entregas/beneficiario/{beneficiarioId}")
    public List<HistorialEntrega> historialPorBeneficiario(@PathVariable Long beneficiarioId) {
        return historialRepo.findByBeneficiarioId(beneficiarioId);
    }
}
