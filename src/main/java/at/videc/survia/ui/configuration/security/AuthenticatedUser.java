package at.videc.survia.ui.configuration.security;

import at.videc.survia.ui.data.User;
import at.videc.survia.ui.data.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    //    @Transactional
    public Optional<User> get() {
//        return authenticationContext.getAuthenticatedUser(UserDetails.class)
//                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()));

        SecurityContext context = SecurityContextHolder.getContext();
        Optional<DefaultOidcUser> oidcUser = authenticationContext.getAuthenticatedUser(DefaultOidcUser.class);
        if (oidcUser.isEmpty()) {
            return Optional.empty();
        } else {
            return oidcUser.map(userDetails -> {
//                    User user = userRepository.findByUsername(userDetails.getEmail());
                        User user = new User();
                        user.setUsername(userDetails.getPreferredUsername());
                        user.setName(userDetails.getFullName());
//                    userRepository.save(user);
                        return user;
                    });
        }


    }

    public void logout() {
        authenticationContext.logout();
    }

}
