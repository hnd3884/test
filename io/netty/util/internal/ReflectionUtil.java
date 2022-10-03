package io.netty.util.internal;

import java.lang.reflect.AccessibleObject;

public final class ReflectionUtil
{
    private ReflectionUtil() {
    }
    
    public static Throwable trySetAccessible(final AccessibleObject object, final boolean checkAccessible) {
        if (checkAccessible && !PlatformDependent0.isExplicitTryReflectionSetAccessible()) {
            return new UnsupportedOperationException("Reflective setAccessible(true) disabled");
        }
        try {
            object.setAccessible(true);
            return null;
        }
        catch (final SecurityException e) {
            return e;
        }
        catch (final RuntimeException e2) {
            return handleInaccessibleObjectException(e2);
        }
    }
    
    private static RuntimeException handleInaccessibleObjectException(final RuntimeException e) {
        if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
            return e;
        }
        throw e;
    }
}
