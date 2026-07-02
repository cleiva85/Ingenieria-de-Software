package com.abrazame.service_auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:9090}")
    private String baseUrl;

    private void enviar(String para, String asunto, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom("proyectoabrazame1@gmail.com");
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("⚠️ Error enviando correo a " + para + ": " + e.getMessage());
        }
    }

    public void enviarSolicitudRecibida(String nombre, String email) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
                <p style="color:rgba(255,255,255,0.85);margin:4px 0 0;font-size:0.85rem;">Sistema de Gestión de Donaciones</p>
              </div>
              <h2 style="color:#1e293b;font-size:1.1rem;">¡Hola, %s!</h2>
              <p style="color:#475569;line-height:1.6;">Tu solicitud para ser voluntario/a de la Fundación Abrázame ha sido recibida correctamente.</p>
              <p style="color:#475569;line-height:1.6;">Nuestro equipo administrativo revisará tu información y te notificaremos por este medio cuando tu cuenta sea activada.</p>
              <div style="background:#EBF5EE;border-left:4px solid #8DC5A0;padding:14px 18px;border-radius:4px;margin:20px 0;">
                <p style="color:#166534;margin:0;font-size:0.88rem;">⏳ <strong>Estado:</strong> Solicitud pendiente de revisión</p>
              </div>
              <p style="color:#94a3b8;font-size:0.78rem;margin-top:24px;">Si no solicitaste ser voluntario/a, puedes ignorar este mensaje.</p>
            </div>
            """.formatted(nombre);
        enviar(email, "✅ Solicitud de voluntariado recibida — Fundación Abrázame", html);
    }

    public void enviarAprobacion(String nombre, String email, String token) {
        String link = baseUrl + "/activar_voluntario.html?token=" + token;
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
                <p style="color:rgba(255,255,255,0.85);margin:4px 0 0;font-size:0.85rem;">Sistema de Gestión de Donaciones</p>
              </div>
              <h2 style="color:#1e293b;font-size:1.1rem;">🎉 ¡Felicitaciones, %s!</h2>
              <p style="color:#475569;line-height:1.6;">Tu solicitud para ser voluntario/a ha sido <strong style="color:#059669;">aprobada</strong>. ¡Bienvenido/a al equipo de la Fundación Abrázame!</p>
              <p style="color:#475569;line-height:1.6;">Para completar tu registro, haz clic en el botón de abajo y crea tu contraseña de acceso:</p>
              <div style="text-align:center;margin:28px 0;">
                <a href="%s" style="background:#8DC5A0;color:#fff;text-decoration:none;padding:14px 32px;border-radius:50px;font-weight:800;font-size:0.95rem;letter-spacing:0.05em;display:inline-block;">
                  🔐 Crear mi contraseña
                </a>
              </div>
              <div style="background:#FEF3C7;border-left:4px solid #F59E0B;padding:14px 18px;border-radius:4px;margin:20px 0;">
                <p style="color:#92400E;margin:0;font-size:0.85rem;">⚠️ Este enlace expira en <strong>24 horas</strong>.</p>
              </div>
              <p style="color:#94a3b8;font-size:0.78rem;margin-top:24px;">Si no puedes hacer clic en el botón, copia este enlace: <br><span style="color:#8DC5A0;">%s</span></p>
            </div>
            """.formatted(nombre, link, link);
        enviar(email, "🎉 ¡Tu cuenta de voluntario/a fue aprobada! — Fundación Abrázame", html);
    }

    public void enviarRechazo(String nombre, String email) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
                <p style="color:rgba(255,255,255,0.85);margin:4px 0 0;font-size:0.85rem;">Sistema de Gestión de Donaciones</p>
              </div>
              <h2 style="color:#1e293b;font-size:1.1rem;">Hola, %s</h2>
              <p style="color:#475569;line-height:1.6;">Luego de revisar tu solicitud, lamentamos informarte que en esta oportunidad no pudimos aprobar tu postulación como voluntario/a de la Fundación Abrázame.</p>
              <p style="color:#475569;line-height:1.6;">Agradecemos tu interés en apoyar nuestra causa.</p>
              <p style="color:#94a3b8;font-size:0.78rem;margin-top:24px;">Fundación Abrázame</p>
            </div>
            """.formatted(nombre);
        enviar(email, "Resultado de tu solicitud — Fundación Abrázame", html);
    }

    /**
     * Reenvía un mensaje del formulario de contacto web a la bandeja de la fundación.
     * El "from" real es proyectoabrazame1@gmail.com (SMTP), pero el asunto incluye
     * el correo del remitente para que el equipo pueda responderle.
     */
    public void enviarMensajeContacto(String nombreRemitente, String emailRemitente,
                                      String asunto, String mensaje) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
                <p style="color:rgba(255,255,255,0.85);margin:4px 0 0;font-size:0.85rem;">Formulario de Contacto Web</p>
              </div>
              <h2 style="color:#1e293b;font-size:1.1rem;">📩 Nuevo mensaje recibido</h2>
              <table style="width:100%%;border-collapse:collapse;font-size:0.88rem;margin-bottom:18px;">
                <tr><td style="padding:6px 12px;color:#64748b;font-weight:700;width:130px;">Nombre</td><td style="padding:6px 12px;">%s</td></tr>
                <tr style="background:#f1f5f9"><td style="padding:6px 12px;color:#64748b;font-weight:700;">Correo</td><td style="padding:6px 12px;"><a href="mailto:%s">%s</a></td></tr>
                <tr><td style="padding:6px 12px;color:#64748b;font-weight:700;">Asunto</td><td style="padding:6px 12px;">%s</td></tr>
              </table>
              <div style="background:#fff;border:1px solid #e2e8f0;border-radius:8px;padding:18px;white-space:pre-wrap;font-size:0.9rem;color:#334155;line-height:1.7;">%s</div>
              <p style="color:#94a3b8;font-size:0.75rem;margin-top:24px;">Mensaje enviado desde el formulario web de abrazame.org</p>
            </div>
            """.formatted(nombreRemitente, emailRemitente, emailRemitente, asunto, mensaje);
        // Enviar a la bandeja interna de la fundación
        enviar("proyectoabrazame1@gmail.com",
               "[Contacto Web] " + asunto + " — de " + nombreRemitente,
               html);
    }
}
