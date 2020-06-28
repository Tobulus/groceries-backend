package remembrall.config.web;

import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SmartHttpSessionIdResolver implements HttpSessionIdResolver {

    private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";
    private static final CookieHttpSessionIdResolver cookie = new CookieHttpSessionIdResolver();
    private static final HeaderHttpSessionIdResolver xauth = HeaderHttpSessionIdResolver.xAuthToken();

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        if (isXAuth(request)) {
            return xauth.resolveSessionIds(request);
        }
        return cookie.resolveSessionIds(request);
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (isXAuth(request)) {
            xauth.setSessionId(request, response, sessionId);
        } else {
            cookie.setSessionId(request, response, sessionId);
        }
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        if (isXAuth(request)) {
            xauth.expireSession(request, response);
        } else {
            cookie.expireSession(request, response);
        }
    }

    private boolean isXAuth(HttpServletRequest request) {
        return request.getHeader(HEADER_X_AUTH_TOKEN) != null;
    }
}
