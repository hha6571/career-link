package com.career.careerlink.common.send;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendHtmlMail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = templateEngine.process("email/" + templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom("your_email@gmail.com");

            mailSender.send(message);

            log.info("HTML 이메일 전송 완료: {}", to);

        } catch (MessagingException e) {
            log.error("HTML 이메일 전송 실패", e);
            throw new IllegalStateException("이메일 전송 실패", e);
        }
    }
}
