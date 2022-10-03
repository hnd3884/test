package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public class TypeAttributeImpl extends AttributeImpl implements TypeAttribute, Cloneable
{
    private String type;
    
    public TypeAttributeImpl() {
        this("word");
    }
    
    public TypeAttributeImpl(final String type) {
        this.type = type;
    }
    
    @Override
    public String type() {
        return this.type;
    }
    
    @Override
    public void setType(final String type) {
        this.type = type;
    }
    
    @Override
    public void clear() {
        this.type = "word";
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof TypeAttributeImpl) {
            final TypeAttributeImpl o = (TypeAttributeImpl)other;
            return (this.type == null) ? (o.type == null) : this.type.equals(o.type);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.type == null) ? 0 : this.type.hashCode();
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final TypeAttribute t = (TypeAttribute)target;
        t.setType(this.type);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(TypeAttribute.class, "type", this.type);
    }
}
