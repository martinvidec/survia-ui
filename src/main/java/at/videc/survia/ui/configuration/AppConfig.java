package at.videc.survia.ui.configuration;

import at.videc.survia.ui.configuration.properties.AppProperties;
import at.videc.survia.restclient.ApiClient;
import at.videc.survia.restclient.api.DatasetEntityControllerApi;
import at.videc.survia.restclient.api.StatusControllerApi;
import at.videc.survia.ui.configuration.security.sso.SSOAccessTokenRefresher;
import at.videc.survia.ui.configuration.security.sso.SSOClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
public class AppConfig {

    private final AppProperties properties;

    public AppConfig(AppProperties properties) {
        this.properties = properties;
    }

    /**
     * RestTemplateBuilder has to be injected in order to be set up for tracing with OpenTelemetry
     * <br>
     * This returns a DatasetEntityControllerApi that is set up to use the correct base path and OAuth2 token
     *
     * @param restTemplateBuilder         RestTemplateBuilder
     * @param ssoClientRequestInterceptor SSOClientRequestInterceptor
     * @return DatasetEntityControllerApi
     */
    @Bean
    public DatasetEntityControllerApi datasetEntityControllerApi(
            @Autowired RestTemplateBuilder restTemplateBuilder,
            @Autowired SSOClientRequestInterceptor ssoClientRequestInterceptor
    ) {
        ApiClient apiClient = getApiClient(restTemplateBuilder, ssoClientRequestInterceptor);
        return new DatasetEntityControllerApi(apiClient);
    }

    /**
     * RestTemplateBuilder has to be injected in order to be set up for tracing with OpenTelemetry
     * <br>
     * This returns a StatusControllerApi that is set up to use the correct base path and OAuth2 token
     *
     * @param restTemplateBuilder         RestTemplateBuilder
     * @param ssoClientRequestInterceptor SSOClientRequestInterceptor
     * @return StatusControllerApi
     */
    @Bean
    public StatusControllerApi statusControllerApi(
            @Autowired RestTemplateBuilder restTemplateBuilder,
            @Autowired SSOClientRequestInterceptor ssoClientRequestInterceptor
    ) {
        ApiClient apiClient = getApiClient(restTemplateBuilder, ssoClientRequestInterceptor);
        return new StatusControllerApi(apiClient);
    }

    private ApiClient getApiClient(RestTemplateBuilder restTemplateBuilder, SSOClientRequestInterceptor ssoClientRequestInterceptor) {
        RestTemplate rest = restTemplateBuilder.build();
        rest.getInterceptors().add(ssoClientRequestInterceptor);

        ApiClient apiClient = new ApiClient(rest);
        apiClient.setBasePath(properties.getNodeUrl());
        return apiClient;
    }

}
