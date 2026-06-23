package com.abrazame.service_gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailNotificacionService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private void enviar(String para, String asunto, String html) {
        if (mailSender == null) { System.out.println("📧 [SIN MAIL] Para: " + para + " | " + asunto); return; }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom("proyectoabrazame1@gmail.com");
            h.setTo(para);
            h.setSubject(asunto);
            h.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) { System.err.println("⚠️ Error enviando correo: " + e.getMessage()); }
    }

    public void enviarDonacionRecibida(String nombreDonante, String emailDonante, String donId, String articulo) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
              </div>
              <h2 style="color:#1e293b;">¡Gracias por tu donación, %s!</h2>
              <p style="color:#475569;line-height:1.6;">Tu donación del artículo <strong>%s</strong> ha sido registrada con el código <strong>%s</strong>.</p>
              <p style="color:#475569;line-height:1.6;">Un voluntario revisará tu donación y nuestro equipo la aprobará pronto. Te notificaremos por correo cuando esté lista.</p>
              <div style="background:#EBF5EE;border-left:4px solid #8DC5A0;padding:14px 18px;border-radius:4px;margin:20px 0;">
                <p style="color:#166534;margin:0;">⏳ <strong>Estado:</strong> Pendiente de revisión</p>
              </div>
              <p style="color:#94a3b8;font-size:0.78rem;">Fundación Abrázame</p>
            </div>
            """.formatted(nombreDonante, articulo, donId);
        enviar(emailDonante, "✅ Donación recibida — Fundación Abrázame", html);
    }

    public void enviarDonacionAprobada(String nombreDonante, String emailDonante, String donId, String articulo) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
              </div>
              <h2 style="color:#1e293b;">🎉 ¡Tu donación fue aprobada, %s!</h2>
              <p style="color:#475569;line-height:1.6;">Tu donación <strong>%s</strong> del artículo <strong>%s</strong> ha sido <strong style="color:#059669;">aprobada</strong>.</p>
              <p style="color:#475569;line-height:1.6;">Ya puedes coordinar el punto y fecha de entrega con nosotros. ¡Gracias por tu generosidad!</p>
              <div style="background:#D1FAE5;border-left:4px solid #059669;padding:14px 18px;border-radius:4px;margin:20px 0;">
                <p style="color:#065F46;margin:0;">✅ <strong>Estado:</strong> Aprobado</p>
              </div>
              <p style="color:#94a3b8;font-size:0.78rem;">Fundación Abrázame</p>
            </div>
            """.formatted(nombreDonante, donId, articulo);
        enviar(emailDonante, "🎉 ¡Tu donación fue aprobada! — Fundación Abrázame", html);
    }

    public void enviarDonacionRechazada(String nombreDonante, String emailDonante, String donId, String articulo, String razon) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9fafb;padding:32px;border-radius:12px;">
              <div style="background:#8DC5A0;padding:20px 28px;border-radius:8px;margin-bottom:24px;">
                <h1 style="color:#fff;margin:0;font-size:1.4rem;">🤗 Fundación Abrázame</h1>
              </div>
              <h2 style="color:#1e293b;">Hola, %s</h2>
              <p style="color:#475569;line-height:1.6;">Lamentamos informarte que tu donación <strong>%s</strong> del artículo <strong>%s</strong> no pudo ser aprobada.</p>
              %s
              <p style="color:#475569;line-height:1.6;">Si tienes dudas, puedes contactarnos directamente.</p>
              <p style="color:#94a3b8;font-size:0.78rem;">Fundación Abrázame</p>
            </div>
            """.formatted(
                nombreDonante, donId, articulo,
                (razon != null && !razon.isBlank())
                    ? "<div style=\"background:#FEE2E2;border-left:4px solid #DC2626;padding:14px 18px;border-radius:4px;margin:20px 0;\"><p style=\"color:#991B1B;margin:0;\">❌ <strong>Razón:</strong> " + razon + "</p></div>"
                    : ""
            );
        enviar(emailDonante, "Resultado de tu donación — Fundación Abrázame", html);
    }
}
