package org.apache.commons.compress.harmony.unpack200.bytecode;

public abstract class CPConstant extends ConstantPoolEntry
{
    private final Object value;
    
    public CPConstant(final byte tag, final Object value, final int globalIndex) {
        super(tag, globalIndex);
        this.value = value;
        if (value == null) {
            throw new NullPointerException("Null arguments are not allowed");
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CPConstant other = (CPConstant)obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        }
        else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }
    
    protected Object getValue() {
        return this.value;
    }
}
