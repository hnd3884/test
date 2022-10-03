package org.owasp.esapi.filters;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import java.util.List;
import java.util.Arrays;
import org.owasp.esapi.errors.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import javax.servlet.Filter;

public class ESAPIFilter implements Filter
{
    private final Logger logger;
    private static final String[] obfuscate;
    
    public ESAPIFilter() {
        this.logger = ESAPI.getLogger("ESAPIFilter");
    }
    
    public void init(final FilterConfig filterConfig) {
        final String path = filterConfig.getInitParameter("resourceDirectory");
        if (path != null) {
            ESAPI.securityConfiguration().setResourceDirectory(path);
        }
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)resp;
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        try {
            try {
                ESAPI.authenticator().login(request, response);
            }
            catch (final AuthenticationException e) {
                ESAPI.authenticator().logout();
                request.setAttribute("message", (Object)"Authentication failed");
                final RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/login.jsp");
                dispatcher.forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            ESAPI.httpUtilities().logHTTPRequest(request, this.logger, Arrays.asList(ESAPIFilter.obfuscate));
            if (!ESAPI.accessController().isAuthorizedForURL(request.getRequestURI())) {
                request.setAttribute("message", (Object)"Unauthorized");
                final RequestDispatcher dispatcher2 = request.getRequestDispatcher("WEB-INF/index.jsp");
                dispatcher2.forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            ESAPI.httpUtilities().setContentType(response);
            ESAPI.httpUtilities().setNoCacheHeaders(response);
        }
        catch (final Exception e2) {
            this.logger.error(Logger.SECURITY_FAILURE, "Error in ESAPI security filter: " + e2.getMessage(), e2);
            request.setAttribute("message", (Object)e2.getMessage());
        }
        finally {
            ESAPI.clearCurrent();
        }
    }
    
    public void destroy() {
    }
    
    static {
        obfuscate = new String[] { "password" };
    }
}
