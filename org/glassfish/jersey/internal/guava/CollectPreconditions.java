package org.glassfish.jersey.internal.guava;

final class CollectPreconditions
{
    static int checkNonnegative(final int value, final String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }
    
    static void checkRemove(final boolean canRemove) {
        Preconditions.checkState(canRemove, (Object)"no calls to next() since the last call to remove()");
    }
}
