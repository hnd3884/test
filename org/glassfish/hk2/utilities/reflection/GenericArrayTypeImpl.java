package org.glassfish.hk2.utilities.reflection;

import org.glassfish.hk2.utilities.general.GeneralUtilities;
import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;

public class GenericArrayTypeImpl implements GenericArrayType
{
    private final Type genericComponentType;
    
    public GenericArrayTypeImpl(final Type gct) {
        this.genericComponentType = gct;
    }
    
    @Override
    public Type getGenericComponentType() {
        return this.genericComponentType;
    }
    
    @Override
    public int hashCode() {
        return this.genericComponentType.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof GenericArrayType)) {
            return false;
        }
        final GenericArrayType other = (GenericArrayType)o;
        return GeneralUtilities.safeEquals(this.genericComponentType, other.getGenericComponentType());
    }
    
    @Override
    public String toString() {
        return "GenericArrayTypeImpl(" + this.genericComponentType + ")";
    }
}
