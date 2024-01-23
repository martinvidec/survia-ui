package at.videc.survia.ui.configuration.security.sso;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

/**
 * Provides default beans for the single-sign-on configuration.
 */
@Configuration
public class SSOBeans {

    /**
     * Provides a default {@link SessionRegistry} bean.
     *
     * @return the session registry bean
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}