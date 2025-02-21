package at.ac.tuwien.sepr.groupphase.backend.service;

import java.io.IOException;

public interface EmailService {

    /**
     * Sends an email to the given email address.
     *
     * @param toEmail the email address to send the email to
     * @param subject the subject of the email
     * @param content the content of the email
     * @throws IOException if an error occurs while sending the email
     */
    void sendEmail(String toEmail, String subject, String content) throws IOException;
}
