package org.cyber_pantera.mailing;

import org.cyber_pantera.entity.User;
import org.springframework.web.util.UriComponentsBuilder;

public class ForgotPasswordEmailContext extends AbstractEmailContext{

    @Override
    public <T> void init(T context) {
        User user = (User) context;
        put("firstName", user.getFirstName());
        setTemplateLocation("mailing/forgot-password");
        setSubject("Reset your password");
        setFrom("asadjon4929@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        final UriComponentsBuilder url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/reset-password").queryParam("token", token);

        put("resetPasswordURL", url.toUriString());
    }
}
