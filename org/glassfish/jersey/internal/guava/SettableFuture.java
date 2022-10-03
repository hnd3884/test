package org.glassfish.jersey.internal.guava;

public final class SettableFuture<V> extends AbstractFuture<V>
{
    private SettableFuture() {
    }
    
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<V>();
    }
    
    public boolean set(final V value) {
        return super.set(value);
    }
    
    public boolean setException(final Throwable throwable) {
        return super.setException(throwable);
    }
}
