package at.videc.survia.ui.configuration;

import at.videc.survia.ui.configuration.properties.AppProperties;
import at.videc.survia.restclient.ApiClient;
import at.videc.survia.restclient.api.DatasetEntityControllerApi;
import at.videc.survia.restclient.api.StatusControllerApi;
import at.videc.survia.ui.configuration.security.sso.SSOAccessTokenRefresher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
public class AppConfig {

    private final AppProperties properties;

    final SSOAccessTokenRefresher ssoAccessTokenRefresher;

    public AppConfig(
            AppProperties properties,
            SSOAccessTokenRefresher ssoAccessTokenRefresher
    ) {
        this.properties = properties;
        this.ssoAccessTokenRefresher = ssoAccessTokenRefresher;
    }

    /**
     * RestTemplateBuilder has to be injected in order to be set up for tracing with OpenTelemetry
     * <br>
     * This returns a DatasetEntityControllerApi that is set up to use the correct base path and OAuth2 token
     *
     * @param restTemplateBuilder RestTemplateBuilder
     * @return DatasetEntityControllerApi
     */
    @Bean
    public DatasetEntityControllerApi datasetEntityControllerApi(
            @Autowired RestTemplateBuilder restTemplateBuilder
    ) {
        ApiClient apiClient = getApiClient(restTemplateBuilder);
        return new DatasetEntityControllerApi(apiClient);
    }

    /**
     * RestTemplateBuilder has to be injected in order to be set up for tracing with OpenTelemetry
     * <br>
     * This returns a StatusControllerApi that is set up to use the correct base path and OAuth2 token
     *
     * @param restTemplateBuilder RestTemplateBuilder
     * @return StatusControllerApi
     */
    @Bean
    public StatusControllerApi statusControllerApi(
            @Autowired RestTemplateBuilder restTemplateBuilder
    ) {
        ApiClient apiClient = getApiClient(restTemplateBuilder);
        return new StatusControllerApi(apiClient);
    }

    private ApiClient getApiClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate rest = restTemplateBuilder.build();

        rest.getInterceptors().add((request, body, execution) -> {

            Optional<OAuth2AccessToken> accessToken = ssoAccessTokenRefresher.refreshAccessToken();
            if (accessToken.isPresent()) {
                request.getHeaders().setBearerAuth(accessToken.get().getTokenValue());
                return execution.execute(request, body);
            }

            return execution.execute(request, body);
        });

        ApiClient apiClient = new ApiClient(rest);
        apiClient.setBasePath(properties.getNodeUrl());
        return apiClient;
    }

}
