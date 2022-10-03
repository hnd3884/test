package org.glassfish.jersey.internal.guava;

final class Iterables
{
    private Iterables() {
    }
    
    public static <T> T getFirst(final Iterable<? extends T> iterable, final T defaultValue) {
        return Iterators.getNext(iterable.iterator(), defaultValue);
    }
}
