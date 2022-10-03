package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

public abstract class ClassResolver
{
    @Nullable
    public abstract Class<?> resolveElementName(@NotNull final String p0, @NotNull final String p1) throws Exception;
}
