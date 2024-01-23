package at.videc.survia.ui.configuration.security.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.authentication.logout.LogoutTokenClaimNames;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SSOLogoutTokenValidator implements OAuth2TokenValidator<Jwt> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SSOLogoutTokenValidator.class);
    private static final String INVALID_REQUEST_ERROR_CODE = "invalid_request";
    private static final String LOGOUT_TOKEN_VALIDATION_URL = "https://openid.net/specs/openid-connect-backchannel-1_0.html#Validation";

    private final ClientRegistration clientRegistration;
    private Duration clockSkew = Duration.ofSeconds(60);
    private Clock clock = Clock.systemUTC();

    public SSOLogoutTokenValidator(ClientRegistration clientRegistration) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        this.clientRegistration = clientRegistration;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        Map<String, Object> invalidClaims = validateRequiredClaims(token);
        if (!invalidClaims.isEmpty()) {
            LOGGER.warn("Logout token validation failed because of invalid claims: {}", invalidClaims);
            return OAuth2TokenValidatorResult.failure(invalidLogoutTokenClaims(invalidClaims));
        }

        if (!validateAlgorithm(token)) {
            return OAuth2TokenValidatorResult.failure(invalidLogoutTokenAlgorithm(token.getHeaders().get("alg").toString()));
        }

        if (!validateIssuer(token)) {
            invalidClaims.put(IdTokenClaimNames.ISS, token.getIssuer());
        }

        if (!validateAudience(token)) {
            invalidClaims.put(IdTokenClaimNames.AUD, token.getAudience());
        }

        if (!validateIssuedAt(token)) {
            invalidClaims.put(IdTokenClaimNames.IAT, token.getIssuedAt());
        }

        if (!validateSubjectOrSid(token)) {
            invalidClaims.put(IdTokenClaimNames.SUB, token.getSubject());
            invalidClaims.put(LogoutTokenClaimNames.SID, token.getClaimAsString(LogoutTokenClaimNames.SID));
        }

        if (!validateEvents(token)) {
            invalidClaims.put(LogoutTokenClaimNames.EVENTS, token.getClaimAsMap(LogoutTokenClaimNames.EVENTS));
        }

        if (token.getClaim(IdTokenClaimNames.NONCE) != null) {
            invalidClaims.put(IdTokenClaimNames.NONCE, token.getClaim(IdTokenClaimNames.NONCE));
        }

        if (!invalidClaims.isEmpty()) {
            LOGGER.warn("Logout token validation failed because of invalid claims: {}", invalidClaims);
            return OAuth2TokenValidatorResult.failure(invalidLogoutTokenClaims(invalidClaims));
        } else {
            LOGGER.debug("Logout token validation succeeded");
            return OAuth2TokenValidatorResult.success();
        }
    }

    private boolean validateIssuer(Jwt token) {
        return token.getIssuer().toExternalForm().equals(clientRegistration.getProviderDetails().getIssuerUri());
    }

    private boolean validateAudience(Jwt token) {
        return token.getAudience().contains(clientRegistration.getClientId());
    }

    private boolean validateIssuedAt(Jwt token) {
        return !Instant.now(clock).plus(clockSkew).isBefore(token.getIssuedAt());
    }

    private boolean validateSubjectOrSid(Jwt token) {
        return token.getSubject() != null || token.getClaimAsString(LogoutTokenClaimNames.SID) != null;
    }

    private boolean validateEvents(Jwt token) {
        Map<String, Object> events = token.getClaimAsMap(LogoutTokenClaimNames.EVENTS);
        return events != null && events.containsKey("http://schemas.openid.net/event/backchannel-logout");
    }

    private boolean validateAlgorithm(Jwt token) {
        return token.getHeaders().get("alg").equals(clientRegistration.getProviderDetails().getConfigurationMetadata().getOrDefault("id_token_signed_response_alg", "RS256"));
    }

    private static OAuth2Error invalidLogoutTokenAlgorithm(String alg) {
        return new OAuth2Error(INVALID_REQUEST_ERROR_CODE, "The Logout Token algorithm is invalid: " + alg, LOGOUT_TOKEN_VALIDATION_URL);
    }

    private static OAuth2Error invalidLogoutTokenClaims(Map<String, Object> invalidClaims) {
        return new OAuth2Error(INVALID_REQUEST_ERROR_CODE, "The Logout Token contains invalid claims: " + invalidClaims, LOGOUT_TOKEN_VALIDATION_URL);
    }

    private static Map<String, Object> validateRequiredClaims(Jwt token) {
        Map<String, Object> requiredClaims = new HashMap<>();
        if (token.getIssuer() == null) {
            requiredClaims.put(IdTokenClaimNames.ISS, null);
        }
        if (token.getIssuedAt() == null) {
            requiredClaims.put(IdTokenClaimNames.IAT, null);
        }
        if (token.getAudience().isEmpty()) {
            requiredClaims.put(IdTokenClaimNames.AUD, null);
        }
        return requiredClaims;
    }
}
