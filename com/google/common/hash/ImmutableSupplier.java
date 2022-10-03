package com.google.common.hash;

import com.google.errorprone.annotations.Immutable;
import com.google.common.base.Supplier;

@Immutable
@ElementTypesAreNonnullByDefault
interface ImmutableSupplier<T> extends Supplier<T>
{
}
