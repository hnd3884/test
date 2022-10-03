package org.apache.catalina.filters;

import java.util.Objects;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class RestCsrfPreventionFilter extends CsrfPreventionFilterBase
{
    private static final Pattern NON_MODIFYING_METHODS_PATTERN;
    private Set<String> pathsAcceptingParams;
    private String pathsDelimiter;
    
    public RestCsrfPreventionFilter() {
        this.pathsAcceptingParams = new HashSet<String>();
        this.pathsDelimiter = ",";
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute("org.apache.catalina.filters.CSRF_REST_NONCE_HEADER_NAME", (Object)"X-CSRF-Token");
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            MethodType mType = MethodType.MODIFYING_METHOD;
            final String method = ((HttpServletRequest)request).getMethod();
            if (method != null && RestCsrfPreventionFilter.NON_MODIFYING_METHODS_PATTERN.matcher(method).matches()) {
                mType = MethodType.NON_MODIFYING_METHOD;
            }
            RestCsrfPreventionStrategy strategy = null;
            switch (mType) {
                case NON_MODIFYING_METHOD: {
                    strategy = new FetchRequest();
                    break;
                }
                default: {
                    strategy = new StateChangingRequest();
                    break;
                }
            }
            if (!strategy.apply((HttpServletRequest)request, (HttpServletResponse)response)) {
                return;
            }
        }
        chain.doFilter(request, response);
    }
    
    public void setPathsAcceptingParams(final String pathsList) {
        if (pathsList != null) {
            for (final String element : pathsList.split(this.pathsDelimiter)) {
                this.pathsAcceptingParams.add(element.trim());
            }
        }
    }
    
    public Set<String> getPathsAcceptingParams() {
        return this.pathsAcceptingParams;
    }
    
    static {
        NON_MODIFYING_METHODS_PATTERN = Pattern.compile("GET|HEAD|OPTIONS");
    }
    
    private enum MethodType
    {
        NON_MODIFYING_METHOD, 
        MODIFYING_METHOD;
    }
    
    private abstract static class RestCsrfPreventionStrategy
    {
        abstract boolean apply(final HttpServletRequest p0, final HttpServletResponse p1) throws IOException;
        
        protected String extractNonceFromRequestHeader(final HttpServletRequest request, final String key) {
            return request.getHeader(key);
        }
        
        protected String[] extractNonceFromRequestParams(final HttpServletRequest request, final String key) {
            return request.getParameterValues(key);
        }
        
        protected void storeNonceToResponse(final HttpServletResponse response, final String key, final String value) {
            response.setHeader(key, value);
        }
        
        protected String extractNonceFromSession(final HttpSession session, final String key) {
            return (session == null) ? null : ((String)session.getAttribute(key));
        }
        
        protected void storeNonceToSession(final HttpSession session, final String key, final Object value) {
            session.setAttribute(key, value);
        }
    }
    
    private class StateChangingRequest extends RestCsrfPreventionStrategy
    {
        public boolean apply(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            if (this.isValidStateChangingRequest(this.extractNonceFromRequest(request), this.extractNonceFromSession(request.getSession(false), "org.apache.catalina.filters.CSRF_REST_NONCE"))) {
                return true;
            }
            this.storeNonceToResponse(response, "X-CSRF-Token", "Required");
            response.sendError(RestCsrfPreventionFilter.this.getDenyStatus(), FilterBase.sm.getString("restCsrfPreventionFilter.invalidNonce"));
            return false;
        }
        
        private boolean isValidStateChangingRequest(final String reqNonce, final String sessionNonce) {
            return reqNonce != null && sessionNonce != null && Objects.equals(reqNonce, sessionNonce);
        }
        
        private String extractNonceFromRequest(final HttpServletRequest request) {
            String nonceFromRequest = this.extractNonceFromRequestHeader(request, "X-CSRF-Token");
            if ((nonceFromRequest == null || Objects.equals("", nonceFromRequest)) && !RestCsrfPreventionFilter.this.getPathsAcceptingParams().isEmpty() && RestCsrfPreventionFilter.this.getPathsAcceptingParams().contains(RestCsrfPreventionFilter.this.getRequestedPath(request))) {
                nonceFromRequest = this.extractNonceFromRequestParams(request);
            }
            return nonceFromRequest;
        }
        
        private String extractNonceFromRequestParams(final HttpServletRequest request) {
            final String[] params = this.extractNonceFromRequestParams(request, "X-CSRF-Token");
            if (params != null && params.length > 0) {
                final String nonce = params[0];
                for (final String param : params) {
                    if (!Objects.equals(param, nonce)) {
                        return null;
                    }
                }
                return nonce;
            }
            return null;
        }
    }
    
    private class FetchRequest extends RestCsrfPreventionStrategy
    {
        public boolean apply(final HttpServletRequest request, final HttpServletResponse response) {
            if ("Fetch".equalsIgnoreCase(this.extractNonceFromRequestHeader(request, "X-CSRF-Token"))) {
                String nonceFromSessionStr = this.extractNonceFromSession(request.getSession(false), "org.apache.catalina.filters.CSRF_REST_NONCE");
                if (nonceFromSessionStr == null) {
                    nonceFromSessionStr = RestCsrfPreventionFilter.this.generateNonce();
                    this.storeNonceToSession(Objects.requireNonNull(request.getSession(true)), "org.apache.catalina.filters.CSRF_REST_NONCE", nonceFromSessionStr);
                }
                this.storeNonceToResponse(response, "X-CSRF-Token", nonceFromSessionStr);
            }
            return true;
        }
    }
}
