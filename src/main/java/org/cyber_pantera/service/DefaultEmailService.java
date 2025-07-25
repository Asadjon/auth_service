package org.cyber_pantera.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.exception.EmailNotSentException;
import org.cyber_pantera.mailing.AbstractEmailContext;
import org.cyber_pantera.mailing.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class DefaultEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    @Override
    public void sendMail(AbstractEmailContext email) {
        var context = new Context();
        context.setVariables(email.getContext());

        try {
            sendMail(email, context);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Email not sent");
        }
    }

    private void sendMail(AbstractEmailContext email, Context contentContext) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        var emailContent = templateEngine.process(email.getTemplateLocation(), contentContext);

        messageHelper.setTo(email.getTo());
        messageHelper.setFrom(email.getFrom());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(emailContent, true);

        mailSender.send(message);
    }
}
