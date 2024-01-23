package at.videc.survia.ui.configuration.security.sso;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.HandlerHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.DefaultRedirectStrategy;

import java.io.IOException;

public class SSORedirectStrategy extends DefaultRedirectStrategy {

    @Override
    public void sendRedirect(HttpServletRequest request,
                             HttpServletResponse response, String url) throws IOException {
        final var servletMapping = request.getHttpServletMapping().getPattern();
        if (HandlerHelper.isFrameworkInternalRequest(servletMapping, request)) {
            UI.getCurrent().getPage().setLocation(url);
        } else {
            super.sendRedirect(request, response, url);
        }
    }

}
