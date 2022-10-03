package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class MethodAccessorImpl extends MagicAccessorImpl implements MethodAccessor
{
    @Override
    public abstract Object invoke(final Object p0, final Object[] p1) throws IllegalArgumentException, InvocationTargetException;
}
