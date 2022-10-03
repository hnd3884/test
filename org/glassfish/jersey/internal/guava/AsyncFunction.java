package org.glassfish.jersey.internal.guava;

interface AsyncFunction<I, O>
{
    ListenableFuture<O> apply(final I p0);
}
