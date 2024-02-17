package at.videc.survia.ui.configuration.security;

import at.videc.survia.ui.data.User;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component
public class AuthenticatedUser {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatedUser.class);

    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    //    @Transactional
    public Optional<User> get() {
        Optional<DefaultOidcUser> oidcUser = authenticationContext.getAuthenticatedUser(DefaultOidcUser.class);
        if (oidcUser.isEmpty()) {
            return Optional.empty();
        } else {
            return oidcUser.map(userDetails -> {
                        User user = new User();
                        user.setUsername(userDetails.getPreferredUsername());
                        user.setName(userDetails.getFullName());
                try {
                    user.setProfilePicture(IOUtils.toByteArray(new URL(userDetails.getPicture())));
                } catch (IOException e) {
                    LOG.warn("Could not load profile picture for user {}", userDetails.getPreferredUsername(), e);
                }
                return user;
                    });
        }


    }

    public void logout() {
        authenticationContext.logout();
    }

}
