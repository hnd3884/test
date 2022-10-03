package com.adventnet.cache.filter;

import com.adventnet.cache.Cache;
import javax.servlet.http.Cookie;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletOutputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import com.adventnet.cache.exception.CacheException;
import com.adventnet.cache.dataobject.CacheObject;
import javax.servlet.ServletException;
import java.io.IOException;
import com.adventnet.cache.dataobject.PageDetailsObject;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public abstract class MPageCacheFilter implements Filter
{
    private boolean cacheEnabled;
    private FilterConfig filterConfig;
    private static final Logger LOGGER;
    
    public MPageCacheFilter() {
        this.cacheEnabled = true;
    }
    
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final long stime = System.currentTimeMillis();
        final HttpServletRequest hreq = (HttpServletRequest)req;
        if (this.cacheEnabled && this.canBeCached(hreq)) {
            MPageCacheFilter.LOGGER.log(Level.FINE, " Yes URI is in the list");
            try {
                final PageDetailsObject pdo = this.getPageDetails(hreq, res, chain);
                if (pdo == null) {
                    MPageCacheFilter.LOGGER.log(Level.INFO, "calling chian.doFilter since page details object is null");
                    chain.doFilter(req, res);
                    return;
                }
                final int status = pdo.getStatus();
                if (status != 200) {
                    return;
                }
                this.writeContent(hreq, res, pdo);
            }
            catch (final Exception exp) {
                MPageCacheFilter.LOGGER.log(Level.SEVERE, "Exception while calling doFilter", exp);
                chain.doFilter(req, res);
            }
        }
        else {
            MPageCacheFilter.LOGGER.log(Level.FINE, " calling chain.dofilter.........: ");
            chain.doFilter(req, res);
        }
        MPageCacheFilter.LOGGER.log(Level.INFO, "Time taken to fetch this page = " + (System.currentTimeMillis() - stime));
    }
    
    protected PageDetailsObject getPageDetails(final HttpServletRequest hreq, final ServletResponse res, final FilterChain chain) throws ServletException, CacheException {
        final String cacheKey = this.getKey(hreq);
        MPageCacheFilter.LOGGER.log(Level.INFO, "key: " + cacheKey);
        final CacheObject cacheValue = (CacheObject)this.getCache().get(cacheKey);
        PageDetailsObject pdo = null;
        try {
            if (cacheValue != null) {
                final Object validityObject = cacheValue.getSourceDetails();
                MPageCacheFilter.LOGGER.log(Level.INFO, "validityObject = " + validityObject);
                final boolean bool = this.isCacheValid(hreq, validityObject, this.getCacheObjectType());
                MPageCacheFilter.LOGGER.log(Level.INFO, "isCacheValid = " + bool);
                if (bool) {
                    pdo = (PageDetailsObject)cacheValue.getValue();
                    MPageCacheFilter.LOGGER.log(Level.SEVERE, "Page is generated from Cache : " + cacheKey);
                }
                else {
                    pdo = this.constructPageDetails(hreq, res, chain, cacheKey, true);
                }
            }
            else {
                pdo = this.constructPageDetails(hreq, res, chain, cacheKey, false);
            }
        }
        catch (final Exception exp) {
            MPageCacheFilter.LOGGER.log(Level.SEVERE, "Exception while fetching page details", exp);
            throw new ServletException(exp.getMessage(), (Throwable)exp);
        }
        return pdo;
    }
    
    protected PageDetailsObject constructPageDetails(final HttpServletRequest hreq, final ServletResponse res, final FilterChain chain, final String cacheKey, final boolean isExist) throws ServletException {
        MPageCacheFilter.LOGGER.log(Level.FINE, " Inside constructPageDetails : ");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            final MCacheResponseWrapper wrapper = new MCacheResponseWrapper((HttpServletResponse)res, baos);
            chain.doFilter((ServletRequest)hreq, (ServletResponse)wrapper);
            wrapper.flush();
            final Object validityObject = this.getValidityObject(hreq, this.getCacheObjectType());
            if (validityObject == null) {
                MPageCacheFilter.LOGGER.log(Level.FINE, "ValidityObject is null");
                return null;
            }
            final PageDetailsObject pdo = new PageDetailsObject(wrapper.getHeaders(), wrapper.getCookies(), wrapper.getContentType(), wrapper.getStatus(), baos.toByteArray());
            final String canBeCached = (String)hreq.getAttribute("canBeCached");
            MPageCacheFilter.LOGGER.log(Level.FINE, " canBeCached attribute: " + canBeCached);
            if (canBeCached != null && canBeCached.equals("false")) {
                if (isExist) {
                    MPageCacheFilter.LOGGER.log(Level.FINE, " removing cache       : ");
                    this.getCache().remove(cacheKey);
                }
            }
            else {
                final CacheObject co = new CacheObject(validityObject, pdo);
                try {
                    final long duration = this.getDuration();
                    if (duration <= 0L) {
                        this.getCache().put(cacheKey, co);
                    }
                    else {
                        this.getCache().put(cacheKey, co, duration);
                    }
                }
                catch (final CacheException ce) {
                    MPageCacheFilter.LOGGER.log(Level.SEVERE, "Exception while constructing page details", ce);
                }
            }
            return pdo;
        }
        catch (final Exception exp) {
            MPageCacheFilter.LOGGER.log(Level.SEVERE, "Exception while constructing page details", exp);
            throw new ServletException(exp.getMessage(), (Throwable)exp);
        }
        finally {
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (final Exception e) {
                    MPageCacheFilter.LOGGER.log(Level.SEVERE, "Exception while constructing page details", e);
                }
            }
        }
    }
    
    protected void writeContent(final HttpServletRequest hreq, final ServletResponse res, final PageDetailsObject pdo) throws IOException {
        final HttpServletResponse hres = (HttpServletResponse)res;
        boolean supportsGZip = false;
        final String acceptEncoding = hreq.getHeader("accept-encoding");
        if (acceptEncoding != null) {
            supportsGZip = (acceptEncoding.indexOf("gzip") > -1);
        }
        MPageCacheFilter.LOGGER.log(Level.INFO, "acceptEncoding " + acceptEncoding);
        MPageCacheFilter.LOGGER.log(Level.INFO, "supportsGZip " + supportsGZip);
        hres.setStatus(pdo.getStatus());
        hres.setContentType(pdo.getType());
        this.setHeaders(hres, pdo.getHeaderList());
        this.setCookies(hres, pdo.getCookieList());
        this.writeBodyContent(hreq, hres, pdo, supportsGZip);
    }
    
    protected void writeBodyContent(final HttpServletRequest hreq, final HttpServletResponse hres, final PageDetailsObject pdo, final boolean supportsGZip) throws IOException {
        ServletOutputStream sos = null;
        sos = hres.getOutputStream();
        if (supportsGZip) {
            hres.addHeader("Content-Encoding", "gzip");
            sos.write(pdo.getCompressedBodyContent());
        }
        else {
            sos.write(pdo.getUnCompressedBodyContent());
        }
    }
    
    private void setHeaders(final HttpServletResponse hres, final List<String[]> headers) {
        if (headers != null) {
            for (final String[] headerValue : headers) {
                hres.addHeader(headerValue[0], headerValue[1]);
            }
        }
    }
    
    private void setCookies(final HttpServletResponse hres, final List<Cookie> cookies) {
        if (cookies != null) {
            for (final Cookie c : cookies) {
                hres.addCookie(c);
            }
        }
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        final String cacheEnabledStr = filterConfig.getInitParameter("isCacheEnabled");
        if (cacheEnabledStr != null) {
            this.cacheEnabled = Boolean.valueOf(cacheEnabledStr);
        }
    }
    
    public abstract boolean canBeCached(final HttpServletRequest p0);
    
    public abstract String getKey(final HttpServletRequest p0) throws CacheException;
    
    public abstract Cache getCache() throws CacheException;
    
    public abstract String getCacheObjectType() throws CacheException;
    
    public abstract Object getValidityObject(final HttpServletRequest p0, final String p1) throws CacheException;
    
    public abstract boolean isCacheValid(final HttpServletRequest p0, final Object p1, final String p2) throws CacheException;
    
    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }
    
    protected long getDuration() {
        return -1L;
    }
    
    static {
        LOGGER = Logger.getLogger(MPageCacheFilter.class.getName());
    }
}
