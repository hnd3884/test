package org.jvnet.hk2.internal;

import java.lang.reflect.Constructor;

public interface ConstructorAction
{
    Object makeMe(final Constructor<?> p0, final Object[] p1, final boolean p2) throws Throwable;
}
