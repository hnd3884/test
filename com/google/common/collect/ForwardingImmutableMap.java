package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
abstract class ForwardingImmutableMap<K, V>
{
    private ForwardingImmutableMap() {
    }
}
