package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum BoundType
{
    OPEN(false), 
    CLOSED(true);
    
    final boolean inclusive;
    
    private BoundType(final boolean inclusive) {
        this.inclusive = inclusive;
    }
    
    static BoundType forBoolean(final boolean inclusive) {
        return inclusive ? BoundType.CLOSED : BoundType.OPEN;
    }
}
