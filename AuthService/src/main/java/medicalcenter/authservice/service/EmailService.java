package medicalcenter.authservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setFrom(fromAddress);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }
}
