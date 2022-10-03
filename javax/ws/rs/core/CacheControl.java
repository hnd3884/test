package javax.ws.rs.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import javax.ws.rs.ext.RuntimeDelegate;

public class CacheControl
{
    private static final RuntimeDelegate.HeaderDelegate<CacheControl> HEADER_DELEGATE;
    private List<String> privateFields;
    private List<String> noCacheFields;
    private Map<String, String> cacheExtension;
    private boolean privateFlag;
    private boolean noCache;
    private boolean noStore;
    private boolean noTransform;
    private boolean mustRevalidate;
    private boolean proxyRevalidate;
    private int maxAge;
    private int sMaxAge;
    
    public CacheControl() {
        this.maxAge = -1;
        this.sMaxAge = -1;
        this.privateFlag = false;
        this.noCache = false;
        this.noStore = false;
        this.noTransform = true;
        this.mustRevalidate = false;
        this.proxyRevalidate = false;
    }
    
    public static CacheControl valueOf(final String value) {
        return CacheControl.HEADER_DELEGATE.fromString(value);
    }
    
    public boolean isMustRevalidate() {
        return this.mustRevalidate;
    }
    
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }
    
    public boolean isProxyRevalidate() {
        return this.proxyRevalidate;
    }
    
    public void setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    public int getSMaxAge() {
        return this.sMaxAge;
    }
    
    public void setSMaxAge(final int sMaxAge) {
        this.sMaxAge = sMaxAge;
    }
    
    public List<String> getNoCacheFields() {
        if (this.noCacheFields == null) {
            this.noCacheFields = new ArrayList<String>();
        }
        return this.noCacheFields;
    }
    
    public void setNoCache(final boolean noCache) {
        this.noCache = noCache;
    }
    
    public boolean isNoCache() {
        return this.noCache;
    }
    
    public boolean isPrivate() {
        return this.privateFlag;
    }
    
    public List<String> getPrivateFields() {
        if (this.privateFields == null) {
            this.privateFields = new ArrayList<String>();
        }
        return this.privateFields;
    }
    
    public void setPrivate(final boolean flag) {
        this.privateFlag = flag;
    }
    
    public boolean isNoTransform() {
        return this.noTransform;
    }
    
    public void setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
    }
    
    public boolean isNoStore() {
        return this.noStore;
    }
    
    public void setNoStore(final boolean noStore) {
        this.noStore = noStore;
    }
    
    public Map<String, String> getCacheExtension() {
        if (this.cacheExtension == null) {
            this.cacheExtension = new HashMap<String, String>();
        }
        return this.cacheExtension;
    }
    
    @Override
    public String toString() {
        return CacheControl.HEADER_DELEGATE.toString(this);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.privateFlag ? 1 : 0);
        hash = 41 * hash + (this.noCache ? 1 : 0);
        hash = 41 * hash + (this.noStore ? 1 : 0);
        hash = 41 * hash + (this.noTransform ? 1 : 0);
        hash = 41 * hash + (this.mustRevalidate ? 1 : 0);
        hash = 41 * hash + (this.proxyRevalidate ? 1 : 0);
        hash = 41 * hash + this.maxAge;
        hash = 41 * hash + this.sMaxAge;
        hash = 41 * hash + hashCodeOf(this.privateFields);
        hash = 41 * hash + hashCodeOf(this.noCacheFields);
        hash = 41 * hash + hashCodeOf(this.cacheExtension);
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
        final CacheControl other = (CacheControl)obj;
        return this.privateFlag == other.privateFlag && this.noCache == other.noCache && this.noStore == other.noStore && this.noTransform == other.noTransform && this.mustRevalidate == other.mustRevalidate && this.proxyRevalidate == other.proxyRevalidate && this.maxAge == other.maxAge && this.sMaxAge == other.sMaxAge && !notEqual(this.privateFields, other.privateFields) && !notEqual(this.noCacheFields, other.noCacheFields) && !notEqual(this.cacheExtension, other.cacheExtension);
    }
    
    private static boolean notEqual(final Collection<?> first, final Collection<?> second) {
        if (first == second) {
            return false;
        }
        if (first == null) {
            return !second.isEmpty();
        }
        if (second == null) {
            return !first.isEmpty();
        }
        return !first.equals(second);
    }
    
    private static boolean notEqual(final Map<?, ?> first, final Map<?, ?> second) {
        if (first == second) {
            return false;
        }
        if (first == null) {
            return !second.isEmpty();
        }
        if (second == null) {
            return !first.isEmpty();
        }
        return !first.equals(second);
    }
    
    private static int hashCodeOf(final Collection<?> instance) {
        return (instance == null || instance.isEmpty()) ? 0 : instance.hashCode();
    }
    
    private static int hashCodeOf(final Map<?, ?> instance) {
        return (instance == null || instance.isEmpty()) ? 0 : instance.hashCode();
    }
    
    static {
        HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class);
    }
}
