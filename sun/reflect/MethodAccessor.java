package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public interface MethodAccessor
{
    Object invoke(final Object p0, final Object[] p1) throws IllegalArgumentException, InvocationTargetException;
}
