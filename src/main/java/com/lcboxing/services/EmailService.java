package com.lcboxing.services;

import com.lcboxing.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final Session session;

    public EmailService() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", Config.EMAIL_HOST);
        props.put("mail.smtp.port", Config.EMAIL_PORT);
        props.put("mail.smtp.ssl.trust", Config.EMAIL_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.EMAIL_USERNAME, Config.EMAIL_PASSWORD);
            }
        });
    }

    /**
     * Envía un email con las credenciales a un nuevo usuario
     */
    public void sendCredentialsEmail(String destinatario, String nombre, String email, String passwordTemporal, String rol) {
        try {
            String asunto = "Bienvenido a LC Boxing - Tus Credenciales de Acceso";
            String cuerpo = buildCredentialsEmailBody(nombre, email, passwordTemporal, rol);

            sendEmail(destinatario, asunto, cuerpo);
            logger.info("Email de credenciales enviado a: {}", destinatario);

        } catch (Exception e) {
            logger.error("Error al enviar email de credenciales a: {}", destinatario, e);
            throw new RuntimeException("Error al enviar email: " + e.getMessage());
        }
    }

    /**
     * Envía un email de bienvenida a un nuevo atleta
     */
    public void sendWelcomeEmail(String destinatario, String nombre) {
        try {
            String asunto = "¡Bienvenido a LC Boxing!";
            String cuerpo = buildWelcomeEmailBody(nombre);

            sendEmail(destinatario, asunto, cuerpo);
            logger.info("Email de bienvenida enviado a: {}", destinatario);

        } catch (Exception e) {
            logger.error("Error al enviar email de bienvenida a: {}", destinatario, e);
        }
    }

    /**
     * Envía notificación de membresía próxima a vencer
     */
    public void sendExpirationWarningEmail(String destinatario, String nombre, String tipoMembresia,
                                           int diasRestantes, String fechaVencimiento) {
        try {
            String asunto = "Recordatorio: Tu membresía está por vencer";
            String cuerpo = buildExpirationWarningBody(nombre, tipoMembresia, diasRestantes, fechaVencimiento);

            sendEmail(destinatario, asunto, cuerpo);
            logger.info("Email de vencimiento enviado a: {}", destinatario);

        } catch (Exception e) {
            logger.error("Error al enviar email de vencimiento a: {}", destinatario, e);
        }
    }

    /**
     * Envía un email genérico
     */
    public void sendEmail(String destinatario, String asunto, String cuerpoHtml) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(Config.EMAIL_FROM, Config.EMAIL_FROM_NAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(asunto);
        message.setContent(cuerpoHtml, "text/html; charset=utf-8");

        Transport.send(message);
        logger.info("Email enviado exitosamente a: {}", destinatario);
    }

    // ============= TEMPLATES DE EMAIL =============

    private String buildCredentialsEmailBody(String nombre, String email, String password, String rol) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; color: white;'>" +
                "<h1 style='margin: 0; font-size: 28px;'>🥊 LC BOXING</h1>" +
                "<p style='margin: 10px 0 0 0; font-size: 14px;'>Sistema de Gestión</p>" +
                "</div>" +
                "<div style='padding: 30px; background-color: #f9f9f9;'>" +
                "<h2 style='color: #667eea;'>¡Bienvenido, " + nombre + "!</h2>" +
                "<p>Se ha creado tu cuenta en el sistema LC Boxing con el rol de <strong>" + rol + "</strong>.</p>" +
                "<div style='background-color: white; padding: 20px; border-left: 4px solid #667eea; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #667eea;'>Tus Credenciales de Acceso</h3>" +
                "<p><strong>Email:</strong> " + email + "</p>" +
                "<p><strong>Password Temporal:</strong> <code style='background: #f4f4f4; padding: 5px 10px; border-radius: 3px; font-size: 16px;'>" + password + "</code></p>" +
                "</div>" +
                "<div style='background-color: #fff3cd; padding: 15px; border-left: 4px solid #ffc107; margin: 20px 0;'>" +
                "<p style='margin: 0;'><strong>⚠️ Importante:</strong> Por seguridad, te recomendamos cambiar tu contraseña después del primer inicio de sesión.</p>" +
                "</div>" +
                "<p>Podrás acceder al sistema para gestionar:</p>" +
                "<ul>" +
                "<li>Atletas y membresías</li>" +
                "<li>Pagos y registros</li>" +
                "<li>Asistencias diarias</li>" +
                "<li>Reportes y estadísticas</li>" +
                "</ul>" +
                "<p>Si tienes alguna pregunta, no dudes en contactarnos.</p>" +
                "</div>" +
                "<div style='background-color: #333; color: white; padding: 20px; text-align: center; font-size: 12px;'>" +
                "<p style='margin: 0;'>© 2025 LC Boxing Gym. Todos los derechos reservados.</p>" +
                "<p style='margin: 10px 0 0 0;'>Este es un correo automático, por favor no responder.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildWelcomeEmailBody(String nombre) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; color: white;'>" +
                "<h1 style='margin: 0; font-size: 28px;'>🥊 LC BOXING</h1>" +
                "</div>" +
                "<div style='padding: 30px;'>" +
                "<h2 style='color: #667eea;'>¡Bienvenido a la familia, " + nombre + "!</h2>" +
                "<p>Estamos emocionados de que formes parte de LC Boxing. Tu registro ha sido completado exitosamente.</p>" +
                "<p>Esperamos verte pronto en el gimnasio. ¡Prepárate para alcanzar tus metas!</p>" +
                "<p>Cualquier duda, estamos a tu disposición.</p>" +
                "<p style='margin-top: 30px;'>¡Nos vemos en el ring!</p>" +
                "<p><strong>El equipo de LC Boxing</strong></p>" +
                "</div>" +
                "<div style='background-color: #333; color: white; padding: 20px; text-align: center; font-size: 12px;'>" +
                "<p style='margin: 0;'>© 2025 LC Boxing Gym</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildExpirationWarningBody(String nombre, String tipoMembresia, int diasRestantes, String fechaVencimiento) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; color: white;'>" +
                "<h1 style='margin: 0; font-size: 28px;'>🥊 LC BOXING</h1>" +
                "</div>" +
                "<div style='padding: 30px;'>" +
                "<h2 style='color: #ff6b6b;'>Recordatorio de Membresía</h2>" +
                "<p>Hola <strong>" + nombre + "</strong>,</p>" +
                "<p>Te recordamos que tu membresía <strong>" + tipoMembresia + "</strong> está próxima a vencer.</p>" +
                "<div style='background-color: #fff3cd; padding: 20px; border-left: 4px solid #ffc107; margin: 20px 0;'>" +
                "<p style='margin: 0; font-size: 18px;'><strong>⏰ Días restantes: " + diasRestantes + "</strong></p>" +
                "<p style='margin: 10px 0 0 0;'>Fecha de vencimiento: <strong>" + fechaVencimiento + "</strong></p>" +
                "</div>" +
                "<p>Para renovar tu membresía y seguir disfrutando de nuestras instalaciones, acércate a recepción o contáctanos.</p>" +
                "<p>¡Gracias por ser parte de LC Boxing!</p>" +
                "</div>" +
                "<div style='background-color: #333; color: white; padding: 20px; text-align: center; font-size: 12px;'>" +
                "<p style='margin: 0;'>© 2025 LC Boxing Gym</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}