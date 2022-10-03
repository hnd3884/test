package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public class FlagsAttributeImpl extends AttributeImpl implements FlagsAttribute, Cloneable
{
    private int flags;
    
    public FlagsAttributeImpl() {
        this.flags = 0;
    }
    
    @Override
    public int getFlags() {
        return this.flags;
    }
    
    @Override
    public void setFlags(final int flags) {
        this.flags = flags;
    }
    
    @Override
    public void clear() {
        this.flags = 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof FlagsAttributeImpl && ((FlagsAttributeImpl)other).flags == this.flags);
    }
    
    @Override
    public int hashCode() {
        return this.flags;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final FlagsAttribute t = (FlagsAttribute)target;
        t.setFlags(this.flags);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(FlagsAttribute.class, "flags", this.flags);
    }
}
