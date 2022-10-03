package org.apache.xerces.impl.dtd;

public class XMLContentSpec
{
    public static final short CONTENTSPECNODE_LEAF = 0;
    public static final short CONTENTSPECNODE_ZERO_OR_ONE = 1;
    public static final short CONTENTSPECNODE_ZERO_OR_MORE = 2;
    public static final short CONTENTSPECNODE_ONE_OR_MORE = 3;
    public static final short CONTENTSPECNODE_CHOICE = 4;
    public static final short CONTENTSPECNODE_SEQ = 5;
    public static final short CONTENTSPECNODE_ANY = 6;
    public static final short CONTENTSPECNODE_ANY_OTHER = 7;
    public static final short CONTENTSPECNODE_ANY_LOCAL = 8;
    public static final short CONTENTSPECNODE_ANY_LAX = 22;
    public static final short CONTENTSPECNODE_ANY_OTHER_LAX = 23;
    public static final short CONTENTSPECNODE_ANY_LOCAL_LAX = 24;
    public static final short CONTENTSPECNODE_ANY_SKIP = 38;
    public static final short CONTENTSPECNODE_ANY_OTHER_SKIP = 39;
    public static final short CONTENTSPECNODE_ANY_LOCAL_SKIP = 40;
    public short type;
    public Object value;
    public Object otherValue;
    
    public XMLContentSpec() {
        this.clear();
    }
    
    public XMLContentSpec(final short n, final Object o, final Object o2) {
        this.setValues(n, o, o2);
    }
    
    public XMLContentSpec(final XMLContentSpec values) {
        this.setValues(values);
    }
    
    public XMLContentSpec(final Provider provider, final int n) {
        this.setValues(provider, n);
    }
    
    public void clear() {
        this.type = -1;
        this.value = null;
        this.otherValue = null;
    }
    
    public void setValues(final short type, final Object value, final Object otherValue) {
        this.type = type;
        this.value = value;
        this.otherValue = otherValue;
    }
    
    public void setValues(final XMLContentSpec xmlContentSpec) {
        this.type = xmlContentSpec.type;
        this.value = xmlContentSpec.value;
        this.otherValue = xmlContentSpec.otherValue;
    }
    
    public void setValues(final Provider provider, final int n) {
        if (!provider.getContentSpec(n, this)) {
            this.clear();
        }
    }
    
    public int hashCode() {
        return this.type << 16 | this.value.hashCode() << 8 | this.otherValue.hashCode();
    }
    
    public boolean equals(final Object o) {
        if (o != null && o instanceof XMLContentSpec) {
            final XMLContentSpec xmlContentSpec = (XMLContentSpec)o;
            return this.type == xmlContentSpec.type && this.value == xmlContentSpec.value && this.otherValue == xmlContentSpec.otherValue;
        }
        return false;
    }
    
    public interface Provider
    {
        boolean getContentSpec(final int p0, final XMLContentSpec p1);
    }
}
