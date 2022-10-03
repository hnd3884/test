package org.glassfish.hk2.utilities.reflection.internal;

import org.glassfish.hk2.utilities.reflection.Pretty;
import java.lang.reflect.Member;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.lang.reflect.Method;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;

public class MethodWrapperImpl implements MethodWrapper
{
    private final Method method;
    private final int hashCode;
    
    public MethodWrapperImpl(final Method method) {
        if (method == null) {
            throw new IllegalArgumentException();
        }
        this.method = method;
        int hashCode = 0;
        hashCode ^= method.getName().hashCode();
        hashCode ^= method.getReturnType().hashCode();
        for (final Class<?> param : method.getParameterTypes()) {
            hashCode ^= param.hashCode();
        }
        this.hashCode = hashCode;
    }
    
    @Override
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof MethodWrapperImpl)) {
            return false;
        }
        final MethodWrapperImpl other = (MethodWrapperImpl)o;
        if (!this.method.getName().equals(other.method.getName())) {
            return false;
        }
        if (!this.method.getReturnType().equals(other.method.getReturnType())) {
            return false;
        }
        final Class<?>[] myParams = this.method.getParameterTypes();
        final Class<?>[] otherParams = other.method.getParameterTypes();
        if (myParams.length != otherParams.length) {
            return false;
        }
        if (ReflectionHelper.isPrivate(this.method) || ReflectionHelper.isPrivate(other.method)) {
            return false;
        }
        for (int lcv = 0; lcv < myParams.length; ++lcv) {
            if (!myParams[lcv].equals(otherParams[lcv])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "MethodWrapperImpl(" + Pretty.method(this.method) + "," + System.identityHashCode(this) + ")";
    }
}
