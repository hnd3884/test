package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public interface ConstructorAccessor
{
    Object newInstance(final Object[] p0) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
