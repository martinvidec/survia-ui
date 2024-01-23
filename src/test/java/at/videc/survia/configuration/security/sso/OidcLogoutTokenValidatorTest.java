package at.videc.survia.configuration.security.sso;

import at.videc.survia.ui.configuration.security.sso.SSOLogoutTokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.oidc.authentication.logout.LogoutTokenClaimNames;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class OidcLogoutTokenValidatorTest {

    @InjectMocks
    private SSOLogoutTokenValidator oidcLogoutTokenValidator;

    @Mock
    private ClientRegistration clientRegistration;

    @Mock
    private ClientRegistration.ProviderDetails providerDetails;

    @Mock
    private Jwt jwt;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(clientRegistration.getClientId()).thenReturn("test-client"); // Ensure this matches the audience in the JWT token
        when(providerDetails.getIssuerUri()).thenReturn("http://localhost:8080/auth/realms/test");
        when(providerDetails.getConfigurationMetadata()).thenReturn(Collections.singletonMap("id_token_signed_response_alg", "RS256"));
    }

    @Test
    public void testValidateWithValidClaims() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));
        when(jwt.getSubject()).thenReturn("subject");
        when(jwt.getClaimAsString(LogoutTokenClaimNames.SID)).thenReturn("sid");
        when(jwt.getClaimAsMap(LogoutTokenClaimNames.EVENTS)).thenReturn(Collections.singletonMap("http://schemas.openid.net/event/backchannel-logout", new Object()));
        when(jwt.getClaim(IdTokenClaimNames.NONCE)).thenReturn(null);

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertFalse(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidClaims() {
        when(jwt.getIssuer()).thenReturn(null);
        when(jwt.getIssuedAt()).thenReturn(null);
        when(jwt.getAudience()).thenReturn(Collections.emptyList());

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidAlgorithm() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "invalid"));

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidIssuer() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/invalid").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidAudience() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("invalid-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidIssuedAt() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now().plusSeconds(61));
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidSubjectAndSid() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));
        when(jwt.getSubject()).thenReturn(null);
        when(jwt.getClaimAsString(LogoutTokenClaimNames.SID)).thenReturn(null);

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithInvalidEvents() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));
        when(jwt.getSubject()).thenReturn("subject");
        when(jwt.getClaimAsString(LogoutTokenClaimNames.SID)).thenReturn("sid");
        when(jwt.getClaimAsMap(LogoutTokenClaimNames.EVENTS)).thenReturn(new HashMap<>());

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidateWithNonceClaim() throws MalformedURLException {
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080/auth/realms/test").toURL());
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getAudience()).thenReturn(java.util.Collections.singletonList("test-client"));
        when(jwt.getHeaders()).thenReturn(Collections.singletonMap("alg", "RS256"));
        when(jwt.getSubject()).thenReturn("subject");
        when(jwt.getClaimAsString(LogoutTokenClaimNames.SID)).thenReturn("sid");
        when(jwt.getClaimAsMap(LogoutTokenClaimNames.EVENTS)).thenReturn(Collections.singletonMap("http://schemas.openid.net/event/backchannel-logout", new Object()));
        when(jwt.getClaim(IdTokenClaimNames.NONCE)).thenReturn("nonce");

        OAuth2TokenValidatorResult result = oidcLogoutTokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }
}