package com.example.NewsApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.NewsApp.dto.RegisterRequest;
import com.example.NewsApp.entity.Ticket;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String ADMIN_EMAIL = "anithamotupalli@gmail.com";


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
    public void sendTicketCreatedEmail(Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ticket.getEmail());
        message.setSubject("Ticket Created: " + ticket.getTitle());
        message.setText("Hello,\n\nYour ticket has been created successfully.\n\nTitle: " + ticket.getTitle() +
                "\nDescription: " + ticket.getDescription() +
                "\nStatus: " + ticket.getTicketStatus() + "\n\nThank you.");
        mailSender.send(message);
    }

    public void sendTicketResolvedEmail(Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ticket.getEmail());
        message.setSubject("Ticket Resolved: " + ticket.getTitle());
        message.setText("Hello,\n\nYour ticket has been resolved successfully.\n\nTitle: " + ticket.getTitle() +
                "\nDescription: " + ticket.getDescription() +
                "\nStatus: " + ticket.getTicketStatus() + "\n\nThank you.");
        mailSender.send(message);
    }

    public void sendRegistrationMail(RegisterRequest request) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(ADMIN_EMAIL);
            helper.setSubject("New Form Submission - User Registration");
            helper.setText(buildEmailTemplate(request), true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildEmailTemplate(RegisterRequest r) {

        return """
            <html>
            <body style="font-family: Arial;">
                <h2>New Registration Form Submitted</h2>
                <table border="1" cellpadding="10" cellspacing="0">
                    <tr><td><b>Name</b></td><td>%s</td></tr>
                    <tr><td><b>Email</b></td><td>%s</td></tr>
                    <tr><td><b>Mobile</b></td><td>%s</td></tr>
                    <tr><td><b>City</b></td><td>%s</td></tr>
                    <tr><td><b>State</b></td><td>%s</td></tr>
                    <tr><td><b>Zip Code</b></td><td>%s</td></tr>
                    <tr><td><b>Latitude</b></td><td>%s</td></tr>
                    <tr><td><b>Longitude</b></td><td>%s</td></tr>
                    <tr><td><b>ID Proof</b></td><td>%s</td></tr>
                    <tr><td><b>ID Number</b></td><td>%s</td></tr>
                    <tr><td><b>Experience</b></td><td>%s</td></tr>
                    <tr><td><b>Specialization</b></td><td>%s</td></tr>
                </table>
            </body>
            </html>
        """.formatted(
                r.getName(),
                r.getEmail(),
                r.getMobileNumber(),
                r.getCity(),
                r.getState(),
                r.getZipCode(),
                r.getLatitude(),
                r.getLongitude(),
                r.getIdProofType(),
                r.getIdProofNumber(),
                r.getExperience(),
                r.getSpecialization()
        );
    }
}

