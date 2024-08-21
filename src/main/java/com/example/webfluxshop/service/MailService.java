package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.MailType;
import com.example.webfluxshop.domain.User;
import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class MailService {

    private final Configuration configuration;
    private final JavaMailSender mailSender;

    public MailService(Configuration configuration, JavaMailSender mailSender) {
        this.configuration = configuration;
        this.mailSender = mailSender;
    }

    public void sendEmail(User user, MailType mailType, Properties params) {
        switch (mailType){
            case REGISTRATION -> sendRegistrationEmail(user,params);
            default -> {}
        }
    }

    @SneakyThrows
    private void sendRegistrationEmail(User user,Properties params) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,false,"UTF-8");
        helper.setSubject("Спасибо за регистрацию в нашем сервисе" + user.getName());
        helper.setTo(user.getEmail());
        String content = getRegistrationEmailContent(user,params);
        helper.setText(content,true);
        mailSender.send(mimeMessage);
        log.info("письмо регистрации отправлено получателю");
    }

    @SneakyThrows
    private String getRegistrationEmailContent(User user,Properties properties) {
        StringWriter writer = new StringWriter();
        Map<String,Object> model = new HashMap<>();
        model.put("name",user.getName());
        configuration.getTemplate("register.ftlh").process(model,writer);
        return writer.getBuffer().toString();
    }
}
