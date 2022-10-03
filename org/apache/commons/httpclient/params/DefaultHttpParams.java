package org.apache.commons.httpclient.params;

import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import java.io.Serializable;

public class DefaultHttpParams implements HttpParams, Serializable, Cloneable
{
    private static final Log LOG;
    private static HttpParamsFactory httpParamsFactory;
    private HttpParams defaults;
    private HashMap parameters;
    
    public static HttpParams getDefaultParams() {
        return DefaultHttpParams.httpParamsFactory.getDefaultParams();
    }
    
    public static void setHttpParamsFactory(final HttpParamsFactory httpParamsFactory) {
        if (httpParamsFactory == null) {
            throw new IllegalArgumentException("httpParamsFactory may not be null");
        }
        DefaultHttpParams.httpParamsFactory = httpParamsFactory;
    }
    
    public DefaultHttpParams(final HttpParams defaults) {
        this.defaults = null;
        this.parameters = null;
        this.defaults = defaults;
    }
    
    public DefaultHttpParams() {
        this(getDefaultParams());
    }
    
    public synchronized HttpParams getDefaults() {
        return this.defaults;
    }
    
    public synchronized void setDefaults(final HttpParams params) {
        this.defaults = params;
    }
    
    public synchronized Object getParameter(final String name) {
        Object param = null;
        if (this.parameters != null) {
            param = this.parameters.get(name);
        }
        if (param != null) {
            return param;
        }
        if (this.defaults != null) {
            return this.defaults.getParameter(name);
        }
        return null;
    }
    
    public synchronized void setParameter(final String name, final Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap();
        }
        this.parameters.put(name, value);
        if (DefaultHttpParams.LOG.isDebugEnabled()) {
            DefaultHttpParams.LOG.debug((Object)("Set parameter " + name + " = " + value));
        }
    }
    
    public synchronized void setParameters(final String[] names, final Object value) {
        for (int i = 0; i < names.length; ++i) {
            this.setParameter(names[i], value);
        }
    }
    
    public long getLongParameter(final String name, final long defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (long)param;
    }
    
    public void setLongParameter(final String name, final long value) {
        this.setParameter(name, new Long(value));
    }
    
    public int getIntParameter(final String name, final int defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (int)param;
    }
    
    public void setIntParameter(final String name, final int value) {
        this.setParameter(name, new Integer(value));
    }
    
    public double getDoubleParameter(final String name, final double defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (double)param;
    }
    
    public void setDoubleParameter(final String name, final double value) {
        this.setParameter(name, new Double(value));
    }
    
    public boolean getBooleanParameter(final String name, final boolean defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (boolean)param;
    }
    
    public void setBooleanParameter(final String name, final boolean value) {
        this.setParameter(name, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isParameterSet(final String name) {
        return this.getParameter(name) != null;
    }
    
    public boolean isParameterSetLocally(final String name) {
        return this.parameters != null && this.parameters.get(name) != null;
    }
    
    public boolean isParameterTrue(final String name) {
        return this.getBooleanParameter(name, false);
    }
    
    public boolean isParameterFalse(final String name) {
        return !this.getBooleanParameter(name, false);
    }
    
    public void clear() {
        this.parameters = null;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DefaultHttpParams clone = (DefaultHttpParams)super.clone();
        if (this.parameters != null) {
            clone.parameters = (HashMap)this.parameters.clone();
        }
        clone.setDefaults(this.defaults);
        return clone;
    }
    
    static {
        LOG = LogFactory.getLog(DefaultHttpParams.class);
        DefaultHttpParams.httpParamsFactory = new DefaultHttpParamsFactory();
    }
}
