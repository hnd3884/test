package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;

public class EntityTag
{
    private static final RuntimeDelegate.HeaderDelegate<EntityTag> HEADER_DELEGATE;
    private String value;
    private boolean weak;
    
    public EntityTag(final String value) {
        this(value, false);
    }
    
    public EntityTag(final String value, final boolean weak) {
        if (value == null) {
            throw new IllegalArgumentException("value==null");
        }
        this.value = value;
        this.weak = weak;
    }
    
    public static EntityTag valueOf(final String value) {
        return EntityTag.HEADER_DELEGATE.fromString(value);
    }
    
    public boolean isWeak() {
        return this.weak;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntityTag)) {
            return super.equals(obj);
        }
        final EntityTag other = (EntityTag)obj;
        return this.value.equals(other.getValue()) && this.weak == other.isWeak();
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + ((this.value != null) ? this.value.hashCode() : 0);
        hash = 17 * hash + (this.weak ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return EntityTag.HEADER_DELEGATE.toString(this);
    }
    
    static {
        HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);
    }
}
