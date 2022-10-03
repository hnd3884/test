package com.sun.xml.internal.bind.v2.model.nav;

import java.util.Arrays;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

final class WildcardTypeImpl implements WildcardType
{
    private final Type[] ub;
    private final Type[] lb;
    
    public WildcardTypeImpl(final Type[] ub, final Type[] lb) {
        this.ub = ub;
        this.lb = lb;
    }
    
    @Override
    public Type[] getUpperBounds() {
        return this.ub;
    }
    
    @Override
    public Type[] getLowerBounds() {
        return this.lb;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.lb) ^ Arrays.hashCode(this.ub);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof WildcardType) {
            final WildcardType that = (WildcardType)obj;
            return Arrays.equals(that.getLowerBounds(), this.lb) && Arrays.equals(that.getUpperBounds(), this.ub);
        }
        return false;
    }
}
