package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface AsyncFunction<I, O>
{
    ListenableFuture<O> apply(@ParametricNullness final I p0) throws Exception;
}
