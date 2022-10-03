package org.glassfish.jersey.internal.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final class Uninterruptibles
{
    private Uninterruptibles() {
    }
    
    public static <V> V getUninterruptibly(final Future<V> future) throws ExecutionException {
        boolean interrupted = false;
        try {
            return future.get();
        }
        catch (final InterruptedException e) {
            interrupted = true;
            return future.get();
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
