package org.cyber_pantera.mailing;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendMail(final AbstractEmailContext email);
}
