package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class ConstructorAccessorImpl extends MagicAccessorImpl implements ConstructorAccessor
{
    @Override
    public abstract Object newInstance(final Object[] p0) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
