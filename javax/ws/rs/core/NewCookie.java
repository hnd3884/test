package javax.ws.rs.core;

import java.util.Date;
import javax.ws.rs.ext.RuntimeDelegate;

public class NewCookie extends Cookie
{
    public static final int DEFAULT_MAX_AGE = -1;
    private static final RuntimeDelegate.HeaderDelegate<NewCookie> delegate;
    private final String comment;
    private final int maxAge;
    private final Date expiry;
    private final boolean secure;
    private final boolean httpOnly;
    
    public NewCookie(final String name, final String value) {
        this(name, value, null, null, 1, null, -1, null, false, false);
    }
    
    public NewCookie(final String name, final String value, final String path, final String domain, final String comment, final int maxAge, final boolean secure) {
        this(name, value, path, domain, 1, comment, maxAge, null, secure, false);
    }
    
    public NewCookie(final String name, final String value, final String path, final String domain, final String comment, final int maxAge, final boolean secure, final boolean httpOnly) {
        this(name, value, path, domain, 1, comment, maxAge, null, secure, httpOnly);
    }
    
    public NewCookie(final String name, final String value, final String path, final String domain, final int version, final String comment, final int maxAge, final boolean secure) {
        this(name, value, path, domain, version, comment, maxAge, null, secure, false);
    }
    
    public NewCookie(final String name, final String value, final String path, final String domain, final int version, final String comment, final int maxAge, final Date expiry, final boolean secure, final boolean httpOnly) {
        super(name, value, path, domain, version);
        this.comment = comment;
        this.maxAge = maxAge;
        this.expiry = expiry;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }
    
    public NewCookie(final Cookie cookie) {
        this(cookie, null, -1, null, false, false);
    }
    
    public NewCookie(final Cookie cookie, final String comment, final int maxAge, final boolean secure) {
        this(cookie, comment, maxAge, null, secure, false);
    }
    
    public NewCookie(final Cookie cookie, final String comment, final int maxAge, final Date expiry, final boolean secure, final boolean httpOnly) {
        super((cookie == null) ? null : cookie.getName(), (cookie == null) ? null : cookie.getValue(), (cookie == null) ? null : cookie.getPath(), (cookie == null) ? null : cookie.getDomain(), (cookie == null) ? 1 : cookie.getVersion());
        this.comment = comment;
        this.maxAge = maxAge;
        this.expiry = expiry;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }
    
    public static NewCookie valueOf(final String value) {
        return NewCookie.delegate.fromString(value);
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public Date getExpiry() {
        return this.expiry;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public boolean isHttpOnly() {
        return this.httpOnly;
    }
    
    public Cookie toCookie() {
        return new Cookie(this.getName(), this.getValue(), this.getPath(), this.getDomain(), this.getVersion());
    }
    
    @Override
    public String toString() {
        return NewCookie.delegate.toString(this);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + ((this.comment != null) ? this.comment.hashCode() : 0);
        hash = 59 * hash + this.maxAge;
        hash = 59 + hash + ((this.expiry != null) ? this.expiry.hashCode() : 0);
        hash = 59 * hash + (this.secure ? 1 : 0);
        hash = 59 * hash + (this.httpOnly ? 1 : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NewCookie other = (NewCookie)obj;
        return (this.getName() == other.getName() || (this.getName() != null && this.getName().equals(other.getName()))) && (this.getValue() == other.getValue() || (this.getValue() != null && this.getValue().equals(other.getValue()))) && this.getVersion() == other.getVersion() && (this.getPath() == other.getPath() || (this.getPath() != null && this.getPath().equals(other.getPath()))) && (this.getDomain() == other.getDomain() || (this.getDomain() != null && this.getDomain().equals(other.getDomain()))) && (this.comment == other.comment || (this.comment != null && this.comment.equals(other.comment))) && this.maxAge == other.maxAge && (this.expiry == other.expiry || (this.expiry != null && this.expiry.equals(other.expiry))) && this.secure == other.secure && this.httpOnly == other.httpOnly;
    }
    
    static {
        delegate = RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class);
    }
}
