package org.apache.catalina.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.security.SecureRandom;
import org.apache.juli.logging.LogFactory;
import java.util.Random;
import org.apache.juli.logging.Log;

public abstract class CsrfPreventionFilterBase extends FilterBase
{
    private final Log log;
    private String randomClass;
    private Random randomSource;
    private int denyStatus;
    
    public CsrfPreventionFilterBase() {
        this.log = LogFactory.getLog((Class)CsrfPreventionFilterBase.class);
        this.randomClass = SecureRandom.class.getName();
        this.denyStatus = 403;
    }
    
    @Override
    protected Log getLogger() {
        return this.log;
    }
    
    public int getDenyStatus() {
        return this.denyStatus;
    }
    
    public void setDenyStatus(final int denyStatus) {
        this.denyStatus = denyStatus;
    }
    
    public void setRandomClass(final String randomClass) {
        this.randomClass = randomClass;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        try {
            final Class<?> clazz = Class.forName(this.randomClass);
            this.randomSource = (Random)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final ReflectiveOperationException e) {
            final ServletException se = new ServletException(CsrfPreventionFilterBase.sm.getString("csrfPrevention.invalidRandomClass", new Object[] { this.randomClass }), (Throwable)e);
            throw se;
        }
    }
    
    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }
    
    protected String generateNonce() {
        final byte[] random = new byte[16];
        final StringBuilder buffer = new StringBuilder();
        this.randomSource.nextBytes(random);
        for (final byte b : random) {
            final byte b2 = (byte)((b & 0xF0) >> 4);
            final byte b3 = (byte)(b & 0xF);
            if (b2 < 10) {
                buffer.append((char)(48 + b2));
            }
            else {
                buffer.append((char)(65 + (b2 - 10)));
            }
            if (b3 < 10) {
                buffer.append((char)(48 + b3));
            }
            else {
                buffer.append((char)(65 + (b3 - 10)));
            }
        }
        return buffer.toString();
    }
    
    protected String getRequestedPath(final HttpServletRequest request) {
        String path = request.getServletPath();
        if (request.getPathInfo() != null) {
            path += request.getPathInfo();
        }
        return path;
    }
}
