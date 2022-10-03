package javax.servlet.http;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.io.Serializable;

public class Cookie implements Cloneable, Serializable
{
    private static final CookieNameValidator validation;
    private static final long serialVersionUID = 1L;
    private final String name;
    private String value;
    private int version;
    private String comment;
    private String domain;
    private int maxAge;
    private String path;
    private boolean secure;
    private boolean httpOnly;
    
    public Cookie(final String name, final String value) {
        this.version = 0;
        this.maxAge = -1;
        Cookie.validation.validate(name);
        this.name = name;
        this.value = value;
    }
    
    public void setComment(final String purpose) {
        this.comment = purpose;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setDomain(final String pattern) {
        this.domain = pattern.toLowerCase(Locale.ENGLISH);
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setMaxAge(final int expiry) {
        this.maxAge = expiry;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public void setPath(final String uri) {
        this.path = uri;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setSecure(final boolean flag) {
        this.secure = flag;
    }
    
    public boolean getSecure() {
        return this.secure;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setValue(final String newValue) {
        this.value = newValue;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int v) {
        this.version = v;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
    
    public boolean isHttpOnly() {
        return this.httpOnly;
    }
    
    static {
        boolean strictServletCompliance;
        String propStrictNaming;
        String propFwdSlashIsSeparator;
        if (System.getSecurityManager() == null) {
            strictServletCompliance = Boolean.getBoolean("org.apache.catalina.STRICT_SERVLET_COMPLIANCE");
            propStrictNaming = System.getProperty("org.apache.tomcat.util.http.ServerCookie.STRICT_NAMING");
            propFwdSlashIsSeparator = System.getProperty("org.apache.tomcat.util.http.ServerCookie.FWD_SLASH_IS_SEPARATOR");
        }
        else {
            strictServletCompliance = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return Boolean.valueOf(System.getProperty("org.apache.catalina.STRICT_SERVLET_COMPLIANCE"));
                }
            });
            propStrictNaming = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("org.apache.tomcat.util.http.ServerCookie.STRICT_NAMING");
                }
            });
            propFwdSlashIsSeparator = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("org.apache.tomcat.util.http.ServerCookie.FWD_SLASH_IS_SEPARATOR");
                }
            });
        }
        boolean strictNaming;
        if (propStrictNaming == null) {
            strictNaming = strictServletCompliance;
        }
        else {
            strictNaming = Boolean.parseBoolean(propStrictNaming);
        }
        boolean allowSlash;
        if (propFwdSlashIsSeparator == null) {
            allowSlash = !strictServletCompliance;
        }
        else {
            allowSlash = !Boolean.parseBoolean(propFwdSlashIsSeparator);
        }
        if (strictNaming) {
            validation = new RFC2109Validator(allowSlash);
        }
        else {
            validation = new RFC6265Validator();
        }
    }
}
