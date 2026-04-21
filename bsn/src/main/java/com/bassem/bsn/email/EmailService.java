package com.bassem.bsn.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Async
    public void sendEmail(String to,
                          String username,//fullName
                          EmailTemplateName emailTemplate,
                          String confirmationUrl,
                          Object activationCode,//Token
                          String subject) throws MessagingException {
        String templateName;
        if (emailTemplate==null){templateName="confirm-email";}
        else{templateName=emailTemplate.getName();}
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name());
        Map<String,Object> properties = new HashMap<>();
        properties.put("username",username);
        properties.put("confirmationUrl",confirmationUrl);
        properties.put("activation_code",activationCode);
        Context context = new Context();
        context.setVariables(properties);
        String template = templateEngine.process(templateName, context);
        helper.setFrom("contact.bassem587@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(template,true);
        mailSender.send(mimeMessage);
    }
    public boolean validateEmail(String email) {
        return isValidEmail(email) && hasMXRecord(email);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    public boolean hasMXRecord(String email) {
        try {
            String domain = email.substring(email.indexOf("@") + 1);

            if (!domain.endsWith(".")) {
                domain = domain + ".";
            }
            // Environment properties for DNS
            java.util.Hashtable<String, String> env = new java.util.Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns:");

            javax.naming.directory.DirContext ictx =
                    new javax.naming.directory.InitialDirContext(env);

            javax.naming.directory.Attributes attrs =
                    ictx.getAttributes(domain, new String[]{"MX"});

            return attrs != null && attrs.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
