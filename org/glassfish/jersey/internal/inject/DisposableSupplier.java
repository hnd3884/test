package org.glassfish.jersey.internal.inject;

import java.util.function.Supplier;

public interface DisposableSupplier<T> extends Supplier<T>
{
    void dispose(final T p0);
}
