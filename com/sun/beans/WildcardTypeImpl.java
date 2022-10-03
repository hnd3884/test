package com.sun.beans;

import java.util.Arrays;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

final class WildcardTypeImpl implements WildcardType
{
    private final Type[] upperBounds;
    private final Type[] lowerBounds;
    
    WildcardTypeImpl(final Type[] upperBounds, final Type[] lowerBounds) {
        this.upperBounds = upperBounds;
        this.lowerBounds = lowerBounds;
    }
    
    @Override
    public Type[] getUpperBounds() {
        return this.upperBounds.clone();
    }
    
    @Override
    public Type[] getLowerBounds() {
        return this.lowerBounds.clone();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType)o;
            return Arrays.equals(this.upperBounds, wildcardType.getUpperBounds()) && Arrays.equals(this.lowerBounds, wildcardType.getLowerBounds());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.upperBounds) ^ Arrays.hashCode(this.lowerBounds);
    }
    
    @Override
    public String toString() {
        Type[] array;
        StringBuilder sb;
        if (this.lowerBounds.length == 0) {
            if (this.upperBounds.length == 0 || Object.class == this.upperBounds[0]) {
                return "?";
            }
            array = this.upperBounds;
            sb = new StringBuilder("? extends ");
        }
        else {
            array = this.lowerBounds;
            sb = new StringBuilder("? super ");
        }
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append(" & ");
            }
            sb.append((array[i] instanceof Class) ? ((Class)array[i]).getName() : array[i].toString());
        }
        return sb.toString();
    }
}
