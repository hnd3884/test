package org.apache.catalina.filters;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serializable;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.HashSet;
import org.apache.juli.logging.LogFactory;
import java.util.Set;
import org.apache.juli.logging.Log;

public class CsrfPreventionFilter extends CsrfPreventionFilterBase
{
    private final Log log;
    private final Set<String> entryPoints;
    private int nonceCacheSize;
    private String nonceRequestParameterName;
    
    public CsrfPreventionFilter() {
        this.log = LogFactory.getLog((Class)CsrfPreventionFilter.class);
        this.entryPoints = new HashSet<String>();
        this.nonceCacheSize = 5;
        this.nonceRequestParameterName = "org.apache.catalina.filters.CSRF_NONCE";
    }
    
    public void setEntryPoints(final String entryPoints) {
        final String[] arr$;
        final String[] values = arr$ = entryPoints.split(",");
        for (final String value : arr$) {
            this.entryPoints.add(value.trim());
        }
    }
    
    public void setNonceCacheSize(final int nonceCacheSize) {
        this.nonceCacheSize = nonceCacheSize;
    }
    
    public void setNonceRequestParameterName(final String parameterName) {
        this.nonceRequestParameterName = parameterName;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        filterConfig.getServletContext().setAttribute("org.apache.catalina.filters.CSRF_NONCE_PARAM_NAME", (Object)this.nonceRequestParameterName);
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        ServletResponse wResponse = null;
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            final HttpServletRequest req = (HttpServletRequest)request;
            final HttpServletResponse res = (HttpServletResponse)response;
            boolean skipNonceCheck = false;
            if ("GET".equals(req.getMethod()) && this.entryPoints.contains(this.getRequestedPath(req))) {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Skipping CSRF nonce-check for GET request to entry point " + this.getRequestedPath(req)));
                }
                skipNonceCheck = true;
            }
            HttpSession session = req.getSession(false);
            LruCache<String> nonceCache = (session == null) ? null : ((LruCache)session.getAttribute("org.apache.catalina.filters.CSRF_NONCE"));
            if (!skipNonceCheck) {
                final String previousNonce = req.getParameter(this.nonceRequestParameterName);
                if (previousNonce == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Rejecting request for " + this.getRequestedPath(req) + ", session " + ((null == session) ? "(none)" : session.getId()) + " with no CSRF nonce found in request"));
                    }
                    res.sendError(this.getDenyStatus());
                    return;
                }
                if (nonceCache == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Rejecting request for " + this.getRequestedPath(req) + ", session " + ((null == session) ? "(none)" : session.getId()) + " due to empty / missing nonce cache"));
                    }
                    res.sendError(this.getDenyStatus());
                    return;
                }
                if (!nonceCache.contains(previousNonce)) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)("Rejecting request for " + this.getRequestedPath(req) + ", session " + ((null == session) ? "(none)" : session.getId()) + " due to invalid nonce " + previousNonce));
                    }
                    res.sendError(this.getDenyStatus());
                    return;
                }
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Allowing request to " + this.getRequestedPath(req) + " with valid CSRF nonce " + previousNonce));
                }
            }
            if (nonceCache == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Creating new CSRF nonce cache with size=" + this.nonceCacheSize + " for session " + ((null == session) ? "(will create)" : session.getId())));
                }
                nonceCache = new LruCache<String>(this.nonceCacheSize);
                if (session == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Creating new session to store CSRF nonce cache");
                    }
                    session = req.getSession(true);
                }
                session.setAttribute("org.apache.catalina.filters.CSRF_NONCE", (Object)nonceCache);
            }
            final String newNonce = this.generateNonce();
            nonceCache.add(newNonce);
            request.setAttribute("org.apache.catalina.filters.CSRF_REQUEST_NONCE", (Object)newNonce);
            wResponse = (ServletResponse)new CsrfResponseWrapper(res, this.nonceRequestParameterName, newNonce);
        }
        else {
            wResponse = response;
        }
        chain.doFilter(request, wResponse);
    }
    
    protected static class CsrfResponseWrapper extends HttpServletResponseWrapper
    {
        private final String nonceRequestParameterName;
        private final String nonce;
        
        public CsrfResponseWrapper(final HttpServletResponse response, final String nonceRequestParameterName, final String nonce) {
            super(response);
            this.nonceRequestParameterName = nonceRequestParameterName;
            this.nonce = nonce;
        }
        
        @Deprecated
        public String encodeRedirectUrl(final String url) {
            return this.encodeRedirectURL(url);
        }
        
        public String encodeRedirectURL(final String url) {
            return this.addNonce(super.encodeRedirectURL(url));
        }
        
        @Deprecated
        public String encodeUrl(final String url) {
            return this.encodeURL(url);
        }
        
        public String encodeURL(final String url) {
            return this.addNonce(super.encodeURL(url));
        }
        
        private String addNonce(final String url) {
            if (url == null || this.nonce == null) {
                return url;
            }
            String path = url;
            String query = "";
            String anchor = "";
            final int pound = path.indexOf(35);
            if (pound >= 0) {
                anchor = path.substring(pound);
                path = path.substring(0, pound);
            }
            final int question = path.indexOf(63);
            if (question >= 0) {
                query = path.substring(question);
                path = path.substring(0, question);
            }
            final StringBuilder sb = new StringBuilder(path);
            if (query.length() > 0) {
                sb.append(query);
                sb.append('&');
            }
            else {
                sb.append('?');
            }
            sb.append(this.nonceRequestParameterName);
            sb.append('=');
            sb.append(this.nonce);
            sb.append(anchor);
            return sb.toString();
        }
    }
    
    protected static class LruCache<T> implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Map<T, T> cache;
        
        public LruCache(final int cacheSize) {
            this.cache = new LinkedHashMap<T, T>() {
                private static final long serialVersionUID = 1L;
                
                @Override
                protected boolean removeEldestEntry(final Map.Entry<T, T> eldest) {
                    return this.size() > cacheSize;
                }
            };
        }
        
        public void add(final T key) {
            synchronized (this.cache) {
                this.cache.put(key, null);
            }
        }
        
        public boolean contains(final T key) {
            synchronized (this.cache) {
                return this.cache.containsKey(key);
            }
        }
    }
}
