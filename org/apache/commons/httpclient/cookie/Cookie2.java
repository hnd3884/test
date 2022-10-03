package org.apache.commons.httpclient.cookie;

import java.util.Date;
import org.apache.commons.httpclient.Cookie;

public class Cookie2 extends Cookie
{
    public static final String DOMAIN = "domain";
    public static final String PATH = "path";
    public static final String PORT = "port";
    public static final String VERSION = "version";
    public static final String SECURE = "secure";
    public static final String MAXAGE = "max-age";
    public static final String COMMENT = "comment";
    public static final String COMMENTURL = "commenturl";
    public static final String DISCARD = "discard";
    private String cookieCommentURL;
    private int[] cookiePorts;
    private boolean discard;
    private boolean hasPortAttribute;
    private boolean isPortAttributeBlank;
    private boolean hasVersionAttribute;
    
    public Cookie2() {
        super(null, "noname", null, null, null, false);
        this.discard = false;
        this.hasPortAttribute = false;
        this.isPortAttributeBlank = false;
        this.hasVersionAttribute = false;
    }
    
    public Cookie2(final String domain, final String name, final String value) {
        super(domain, name, value);
        this.discard = false;
        this.hasPortAttribute = false;
        this.isPortAttributeBlank = false;
        this.hasVersionAttribute = false;
    }
    
    public Cookie2(final String domain, final String name, final String value, final String path, final Date expires, final boolean secure) {
        super(domain, name, value, path, expires, secure);
        this.discard = false;
        this.hasPortAttribute = false;
        this.isPortAttributeBlank = false;
        this.hasVersionAttribute = false;
    }
    
    public Cookie2(final String domain, final String name, final String value, final String path, final Date expires, final boolean secure, final int[] ports) {
        super(domain, name, value, path, expires, secure);
        this.discard = false;
        this.hasPortAttribute = false;
        this.isPortAttributeBlank = false;
        this.hasVersionAttribute = false;
        this.setPorts(ports);
    }
    
    public String getCommentURL() {
        return this.cookieCommentURL;
    }
    
    public void setCommentURL(final String commentURL) {
        this.cookieCommentURL = commentURL;
    }
    
    public int[] getPorts() {
        return this.cookiePorts;
    }
    
    public void setPorts(final int[] ports) {
        this.cookiePorts = ports;
    }
    
    public void setDiscard(final boolean toDiscard) {
        this.discard = toDiscard;
    }
    
    public boolean isPersistent() {
        return null != this.getExpiryDate() && !this.discard;
    }
    
    public void setPortAttributeSpecified(final boolean value) {
        this.hasPortAttribute = value;
    }
    
    public boolean isPortAttributeSpecified() {
        return this.hasPortAttribute;
    }
    
    public void setPortAttributeBlank(final boolean value) {
        this.isPortAttributeBlank = value;
    }
    
    public boolean isPortAttributeBlank() {
        return this.isPortAttributeBlank;
    }
    
    public void setVersionAttributeSpecified(final boolean value) {
        this.hasVersionAttribute = value;
    }
    
    public boolean isVersionAttributeSpecified() {
        return this.hasVersionAttribute;
    }
    
    public String toExternalForm() {
        final CookieSpec spec = CookiePolicy.getCookieSpec("rfc2965");
        return spec.formatCookie(this);
    }
}
