package at.videc.survia.ui.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.*;

public class KeycloakRoleMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        for (GrantedAuthority authority : authorities) {
            // Keycloak roles can be prefixed with 'ROLE_'. Remove this prefix to prevent doubling it.
            if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                Map<String, Object> map = oidcUserAuthority.getIdToken().getClaimAsMap("realm_access");
                if(!map.isEmpty()) {
                    Object roleObject = map.get("roles");
                    if(roleObject instanceof List<?> roleObjectList) {
                        roleObjectList.forEach(object -> {
                            if(object instanceof String role) {
                                mappedAuthorities.add(new SimpleGrantedAuthority(role));
                            }
                        });
                    }
                }
            }
        }

        return mappedAuthorities;
    }
}
