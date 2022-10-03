package org.glassfish.hk2.utilities.reflection;

import java.util.Arrays;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

public class ParameterizedTypeImpl implements ParameterizedType
{
    private final Type rawType;
    private final Type[] actualTypeArguments;
    
    public ParameterizedTypeImpl(final Type rawType, final Type... actualTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
    }
    
    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments;
    }
    
    @Override
    public Type getRawType() {
        return this.rawType;
    }
    
    @Override
    public Type getOwnerType() {
        return null;
    }
    
    @Override
    public int hashCode() {
        final int retVal = Arrays.hashCode(this.actualTypeArguments);
        if (this.rawType == null) {
            return retVal;
        }
        return retVal ^ this.rawType.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType other = (ParameterizedType)o;
        if (!this.rawType.equals(other.getRawType())) {
            return false;
        }
        final Type[] otherActuals = other.getActualTypeArguments();
        if (otherActuals.length != this.actualTypeArguments.length) {
            return false;
        }
        for (int lcv = 0; lcv < otherActuals.length; ++lcv) {
            if (!this.actualTypeArguments[lcv].equals(otherActuals[lcv])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return Pretty.pType(this);
    }
}
