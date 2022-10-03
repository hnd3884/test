package jdk.internal.dynalink.linker;

import java.lang.invoke.MethodHandle;

public interface MethodHandleTransformer
{
    MethodHandle transform(final MethodHandle p0);
}
