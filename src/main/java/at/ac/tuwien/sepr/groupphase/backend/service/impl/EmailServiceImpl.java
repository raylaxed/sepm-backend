package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.SendGrid;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.sendgrid.api.key}")
    private String sendgridApiKey;

    @Override
    public void sendEmail(String toEmail, String subject, String content) throws IOException {
        Email from = new Email("sepr.ticketline@proton.me"); // Sender's email
        Email recipient = new Email(toEmail); // Recipient's email
        Content emailContent = new Content("text/plain", content); // Email body
        Mail mail = new Mail(from, subject, recipient, emailContent);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            logger.info("Response Code: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());
            logger.info("Response Headers: {}", response.getHeaders());
        } catch (IOException ex) {
            logger.error("Error while sending email", ex);
            throw ex;
        }
    }
}