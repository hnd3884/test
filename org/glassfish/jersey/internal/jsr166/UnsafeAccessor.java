package org.glassfish.jersey.internal.jsr166;

import java.lang.reflect.Field;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import sun.misc.Unsafe;

class UnsafeAccessor
{
    static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (final SecurityException ex) {
            try {
                return AccessController.doPrivileged(() -> {
                    final Class<Unsafe> k = Unsafe.class;
                    k.getDeclaredFields();
                    final Field[] array;
                    final int length = array.length;
                    int i = 0;
                    while (i < length) {
                        final Field f = array[i];
                        f.setAccessible(true);
                        final Object x = f.get(null);
                        if (k.isInstance(x)) {
                            return (Unsafe)k.cast(x);
                        }
                        else {
                            ++i;
                        }
                    }
                    throw new NoSuchFieldError("the Unsafe");
                });
            }
            catch (final PrivilegedActionException e) {
                throw new RuntimeException("Could not initialize intrinsics", e.getCause());
            }
        }
    }
}
