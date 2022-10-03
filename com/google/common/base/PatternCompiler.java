package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
interface PatternCompiler
{
    CommonPattern compile(final String p0);
    
    boolean isPcreLike();
}
