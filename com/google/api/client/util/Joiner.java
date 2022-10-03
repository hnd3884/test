package com.google.api.client.util;

public final class Joiner
{
    private final com.google.common.base.Joiner wrapped;
    
    public static Joiner on(final char separator) {
        return new Joiner(com.google.common.base.Joiner.on(separator));
    }
    
    private Joiner(final com.google.common.base.Joiner wrapped) {
        this.wrapped = wrapped;
    }
    
    public final String join(final Iterable<?> parts) {
        return this.wrapped.join((Iterable)parts);
    }
}
