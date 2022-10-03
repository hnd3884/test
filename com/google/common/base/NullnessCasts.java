package com.google.common.base;

import javax.annotation.CheckForNull;
import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class NullnessCasts
{
    @ParametricNullness
    static <T> T uncheckedCastNullableTToT(@CheckForNull final T t) {
        return t;
    }
    
    private NullnessCasts() {
    }
}
