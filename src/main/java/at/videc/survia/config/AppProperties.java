package at.videc.survia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "survia")
public class AppProperties {

    private String nodeUrl;
    private Long heartbeatInterval;
    private String version;
    private String apiVersion;
    private String jwtVersion;

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public Long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getJwtVersion() {
        return jwtVersion;
    }

    public void setJwtVersion(String jwtVersion) {
        this.jwtVersion = jwtVersion;
    }
}
