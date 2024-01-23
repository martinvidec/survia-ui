package at.videc.survia.ui.configuration.security.sso;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides configuration properties for the single-sign-on configuration.
 */
@ConfigurationProperties(prefix = SSOProperties.PREFIX)
public class SSOProperties {

    /**
     * The prefix for the single-sign-on configuration properties.
     */
    public static final String PREFIX = "survia.sso";

    /**
     * The default login route.
     */
    static final String DEFAULT_LOGIN_ROUTE = "/login";

    /**
     * The default logout-redirect route.
     */
    static final String DEFAULT_LOGOUT_REDIRECT_ROUTE = "{baseUrl}";

    /**
     * The default Back-Channel Logout route. This should be the same as in the
     * OIDC provider's configuration to be able to accept logout notices as
     * described by the specification. It requires a URI variable to match the
     * client registration-id: {@code registrationId}.
     *
     * @see <a href="https://openid.net/specs/openid-connect-backchannel-1_0.html">https://openid.net/specs/openid-connect-backchannel-1_0.html</a>
     */
    static final String DEFAULT_BACKCHANNEL_LOGOUT_ROUTE = "/logout/back-channel/{" + SSOBackChannelLogoutFilter.REGISTRATION_ID + "}";

    /**
     * The default maximum number of concurrent sessions allowed per user: -1
     * means any number of concurrent sessions is allowed.
     */
    static final int DEFAULT_MAXIMUM_SESSIONS_PER_USER = -1;


    /**
     * The route to redirect unauthorized requests to.
     */
    private String loginRoute = DEFAULT_LOGIN_ROUTE;

    /**
     * The route to redirect to after successful logout.
     */
    private String logoutRedirectRoute = DEFAULT_LOGOUT_REDIRECT_ROUTE;

    /**
     * If set to {@code true} it enables support for Back-Channel logout.
     */
    private boolean backChannelLogout = false;

    /**
     * The route to receive Back-Channel logout notices.
     */
    private String backChannelLogoutRoute = DEFAULT_BACKCHANNEL_LOGOUT_ROUTE;

    /**
     * The maximum number of concurrent sessions allowed per user.
     */
    private int maximumConcurrentSessions = DEFAULT_MAXIMUM_SESSIONS_PER_USER;

    /**
     * Gets the login route.
     *
     * @return the login route
     */
    public String getLoginRoute() {
        return loginRoute;
    }

    /**
     * Sets the login route.
     *
     * @param loginRoute
     *            the login route
     */
    public void setLoginRoute(String loginRoute) {
        this.loginRoute = loginRoute;
    }

    /**
     * Gets the logout-redirect route.
     *
     * @return the logout-redirect route
     */
    public String getLogoutRedirectRoute() {
        return logoutRedirectRoute;
    }

    /**
     * Sets the logout-redirect route.
     *
     * @param logoutRedirectRoute
     *            the logout-redirect route
     */
    public void setLogoutRedirectRoute(String logoutRedirectRoute) {
        this.logoutRedirectRoute = logoutRedirectRoute;
    }

    /**
     * Checks if Back-Channel logout is enabled.
     *
     * @return true, if Back-Channel logout is enabled
     */
    public boolean isBackChannelLogout() {
        return backChannelLogout;
    }

    /**
     * Enables (or disables) Back-Channel logout.
     *
     * @param backChannelLogout
     *            true, if Back-Channel logout should be enabled
     */
    public void setBackChannelLogout(boolean backChannelLogout) {
        this.backChannelLogout = backChannelLogout;
    }

    /**
     * Gets the Back-Channel logout route.
     *
     * @return the Back-Channel logout route
     */
    public String getBackChannelLogoutRoute() {
        return backChannelLogoutRoute;
    }

    /**
     * Sets the Back-Channel logout route.
     *
     * @param backchannelLogoutRoute
     *            the Back-Channel logout route
     */
    public void setBackChannelLogoutRoute(String backchannelLogoutRoute) {
        this.backChannelLogoutRoute = backchannelLogoutRoute;
    }

    /**
     * Gets the maximum number of concurrent sessions allowed per user.
     *
     * @return the maximum number of concurrent sessions allowed per user
     */
    public int getMaximumConcurrentSessions() {
        return maximumConcurrentSessions;
    }

    /**
     * Sets the maximum number of concurrent sessions allowed per user.
     *
     * @param maximumConcurrentSessions
     *            the maximum number of concurrent sessions allowed per user
     */
    public void setMaximumConcurrentSessions(int maximumConcurrentSessions) {
        this.maximumConcurrentSessions = maximumConcurrentSessions;
    }
}
