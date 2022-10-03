package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.util.LangUtils;
import org.apache.commons.logging.Log;
import java.util.Date;
import java.util.Comparator;
import java.io.Serializable;

public class Cookie extends NameValuePair implements Serializable, Comparator
{
    private String cookieComment;
    private String cookieDomain;
    private Date cookieExpiryDate;
    private String cookiePath;
    private boolean isSecure;
    private boolean hasPathAttribute;
    private boolean hasDomainAttribute;
    private int cookieVersion;
    private static final Log LOG;
    
    public Cookie() {
        this(null, "noname", null, null, null, false);
    }
    
    public Cookie(final String domain, final String name, final String value) {
        this(domain, name, value, null, null, false);
    }
    
    public Cookie(final String domain, final String name, final String value, final String path, final Date expires, final boolean secure) {
        super(name, value);
        this.hasPathAttribute = false;
        this.hasDomainAttribute = false;
        this.cookieVersion = 0;
        Cookie.LOG.trace((Object)"enter Cookie(String, String, String, String, Date, boolean)");
        if (name == null) {
            throw new IllegalArgumentException("Cookie name may not be null");
        }
        if (name.trim().equals("")) {
            throw new IllegalArgumentException("Cookie name may not be blank");
        }
        this.setPath(path);
        this.setDomain(domain);
        this.setExpiryDate(expires);
        this.setSecure(secure);
    }
    
    public Cookie(final String domain, final String name, final String value, final String path, final int maxAge, final boolean secure) {
        this(domain, name, value, path, null, secure);
        if (maxAge < -1) {
            throw new IllegalArgumentException("Invalid max age:  " + Integer.toString(maxAge));
        }
        if (maxAge >= 0) {
            this.setExpiryDate(new Date(System.currentTimeMillis() + maxAge * 1000L));
        }
    }
    
    public String getComment() {
        return this.cookieComment;
    }
    
    public void setComment(final String comment) {
        this.cookieComment = comment;
    }
    
    public Date getExpiryDate() {
        return this.cookieExpiryDate;
    }
    
    public void setExpiryDate(final Date expiryDate) {
        this.cookieExpiryDate = expiryDate;
    }
    
    public boolean isPersistent() {
        return null != this.cookieExpiryDate;
    }
    
    public String getDomain() {
        return this.cookieDomain;
    }
    
    public void setDomain(String domain) {
        if (domain != null) {
            final int ndx = domain.indexOf(":");
            if (ndx != -1) {
                domain = domain.substring(0, ndx);
            }
            this.cookieDomain = domain.toLowerCase();
        }
    }
    
    public String getPath() {
        return this.cookiePath;
    }
    
    public void setPath(final String path) {
        this.cookiePath = path;
    }
    
    public boolean getSecure() {
        return this.isSecure;
    }
    
    public void setSecure(final boolean secure) {
        this.isSecure = secure;
    }
    
    public int getVersion() {
        return this.cookieVersion;
    }
    
    public void setVersion(final int version) {
        this.cookieVersion = version;
    }
    
    public boolean isExpired() {
        return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= System.currentTimeMillis();
    }
    
    public boolean isExpired(final Date now) {
        return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= now.getTime();
    }
    
    public void setPathAttributeSpecified(final boolean value) {
        this.hasPathAttribute = value;
    }
    
    public boolean isPathAttributeSpecified() {
        return this.hasPathAttribute;
    }
    
    public void setDomainAttributeSpecified(final boolean value) {
        this.hasDomainAttribute = value;
    }
    
    public boolean isDomainAttributeSpecified() {
        return this.hasDomainAttribute;
    }
    
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.getName());
        hash = LangUtils.hashCode(hash, this.cookieDomain);
        hash = LangUtils.hashCode(hash, this.cookiePath);
        return hash;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Cookie) {
            final Cookie that = (Cookie)obj;
            return LangUtils.equals(this.getName(), that.getName()) && LangUtils.equals(this.cookieDomain, that.cookieDomain) && LangUtils.equals(this.cookiePath, that.cookiePath);
        }
        return false;
    }
    
    public String toExternalForm() {
        CookieSpec spec = null;
        if (this.getVersion() > 0) {
            spec = CookiePolicy.getDefaultSpec();
        }
        else {
            spec = CookiePolicy.getCookieSpec("netscape");
        }
        return spec.formatCookie(this);
    }
    
    public int compare(final Object o1, final Object o2) {
        Cookie.LOG.trace((Object)"enter Cookie.compare(Object, Object)");
        if (!(o1 instanceof Cookie)) {
            throw new ClassCastException(o1.getClass().getName());
        }
        if (!(o2 instanceof Cookie)) {
            throw new ClassCastException(o2.getClass().getName());
        }
        final Cookie c1 = (Cookie)o1;
        final Cookie c2 = (Cookie)o2;
        if (c1.getPath() == null && c2.getPath() == null) {
            return 0;
        }
        if (c1.getPath() == null) {
            if (c2.getPath().equals("/")) {
                return 0;
            }
            return -1;
        }
        else {
            if (c2.getPath() != null) {
                return c1.getPath().compareTo(c2.getPath());
            }
            if (c1.getPath().equals("/")) {
                return 0;
            }
            return 1;
        }
    }
    
    public String toString() {
        return this.toExternalForm();
    }
    
    static {
        LOG = LogFactory.getLog(Cookie.class);
    }
}
