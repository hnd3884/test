package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.NamespaceContext;

public interface NamespaceContext2 extends NamespaceContext
{
    String declareNamespace(final String p0, final String p1, final boolean p2);
    
    int force(@NotNull final String p0, @NotNull final String p1);
}
