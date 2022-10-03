package com.google.api.client.util;

public final class Throwables
{
    public static RuntimeException propagate(final Throwable throwable) {
        return com.google.common.base.Throwables.propagate(throwable);
    }
    
    public static void propagateIfPossible(final Throwable throwable) {
        if (throwable != null) {
            com.google.common.base.Throwables.throwIfUnchecked(throwable);
        }
    }
    
    public static <X extends Throwable> void propagateIfPossible(final Throwable throwable, final Class<X> declaredType) throws X, Throwable {
        com.google.common.base.Throwables.propagateIfPossible(throwable, (Class)declaredType);
    }
    
    private Throwables() {
    }
}
