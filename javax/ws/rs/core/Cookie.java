package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;

public class Cookie
{
    public static final int DEFAULT_VERSION = 1;
    private static final RuntimeDelegate.HeaderDelegate<Cookie> HEADER_DELEGATE;
    private final String name;
    private final String value;
    private final int version;
    private final String path;
    private final String domain;
    
    public Cookie(final String name, final String value, final String path, final String domain, final int version) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("name==null");
        }
        this.name = name;
        this.value = value;
        this.version = version;
        this.domain = domain;
        this.path = path;
    }
    
    public Cookie(final String name, final String value, final String path, final String domain) throws IllegalArgumentException {
        this(name, value, path, domain, 1);
    }
    
    public Cookie(final String name, final String value) throws IllegalArgumentException {
        this(name, value, null, null);
    }
    
    public static Cookie valueOf(final String value) {
        return Cookie.HEADER_DELEGATE.fromString(value);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public String getPath() {
        return this.path;
    }
    
    @Override
    public String toString() {
        return Cookie.HEADER_DELEGATE.toString(this);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + ((this.name != null) ? this.name.hashCode() : 0);
        hash = 97 * hash + ((this.value != null) ? this.value.hashCode() : 0);
        hash = 97 * hash + this.version;
        hash = 97 * hash + ((this.path != null) ? this.path.hashCode() : 0);
        hash = 97 * hash + ((this.domain != null) ? this.domain.hashCode() : 0);
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
        final Cookie other = (Cookie)obj;
        return (this.name == other.name || (this.name != null && this.name.equals(other.name))) && (this.value == other.value || (this.value != null && this.value.equals(other.value))) && this.version == other.version && (this.path == other.path || (this.path != null && this.path.equals(other.path))) && (this.domain == other.domain || (this.domain != null && this.domain.equals(other.domain)));
    }
    
    static {
        HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(Cookie.class);
    }
}
