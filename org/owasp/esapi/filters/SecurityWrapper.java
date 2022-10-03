package org.owasp.esapi.filters;

import org.owasp.esapi.StringUtilities;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import javax.servlet.Filter;

public class SecurityWrapper implements Filter
{
    private final Logger logger;
    private String allowableResourcesRoot;
    
    public SecurityWrapper() {
        this.logger = ESAPI.getLogger("SecurityWrapper");
        this.allowableResourcesRoot = "WEB-INF";
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            final HttpServletRequest hrequest = (HttpServletRequest)request;
            final HttpServletResponse hresponse = (HttpServletResponse)response;
            final SecurityWrapperRequest secureRequest = new SecurityWrapperRequest(hrequest);
            final SecurityWrapperResponse secureResponse = new SecurityWrapperResponse(hresponse);
            secureRequest.setAllowableContentRoot(this.allowableResourcesRoot);
            ESAPI.httpUtilities().setCurrentHTTP((HttpServletRequest)secureRequest, (HttpServletResponse)secureResponse);
            chain.doFilter((ServletRequest)ESAPI.currentRequest(), (ServletResponse)ESAPI.currentResponse());
        }
        catch (final Exception e) {
            this.logger.error(Logger.SECURITY_FAILURE, "Error in SecurityWrapper: " + e.getMessage(), e);
            request.setAttribute("message", (Object)e.getMessage());
        }
        finally {
            ESAPI.httpUtilities().clearCurrent();
        }
    }
    
    public void destroy() {
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.allowableResourcesRoot = StringUtilities.replaceNull(filterConfig.getInitParameter("allowableResourcesRoot"), this.allowableResourcesRoot);
    }
}
