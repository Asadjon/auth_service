package org.cyber_pantera.mailing;

import org.cyber_pantera.entity.User;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context) {
        User user = (User) context;
        put("firstName", user.getFirstName());
        setTemplateLocation("mailing/email-verification");
        setSubject("Complete Your Registration");
        setFrom("asadjon4929@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        final UriComponentsBuilder url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/api/v1/auth/confirm").queryParam("token", token);

        put("confirmationURL", url.toUriString());
    }
}
