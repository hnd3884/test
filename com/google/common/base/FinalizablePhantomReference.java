package com.google.common.base;

import java.lang.ref.ReferenceQueue;
import javax.annotation.CheckForNull;
import com.google.common.annotations.GwtIncompatible;
import java.lang.ref.PhantomReference;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class FinalizablePhantomReference<T> extends PhantomReference<T> implements FinalizableReference
{
    protected FinalizablePhantomReference(@CheckForNull final T referent, final FinalizableReferenceQueue queue) {
        super(referent, queue.queue);
        queue.cleanUp();
    }
}
