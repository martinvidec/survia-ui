package at.videc.survia.ui.configuration.security.sso;

import com.vaadin.flow.server.HandlerHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

public class SSOExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSOExpiredSessionStrategy.class);

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        final HttpServletRequest request = event.getRequest();
        final HttpServletResponse response = event.getResponse();
        final String redirectRoute = '/' + request.getContextPath();
        final String servletMapping = request.getHttpServletMapping().getPattern();
        if (HandlerHelper.isFrameworkInternalRequest(servletMapping, request)) {
            LOGGER.debug("Session expired during internal request: writing " + "a Vaadin-Refresh token to the response body");
            response.getWriter().write("Vaadin-Refresh: " + redirectRoute);
        } else {
            LOGGER.debug("Session expired: redirecting to " + redirectRoute);
            response.setStatus(302);
            response.setHeader("Location", redirectRoute);
        }
    }

}
