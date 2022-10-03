package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;

public interface IQualifiedTypeResolutionListener
{
    void recordResolution(final QualifiedTypeReference p0, final TypeBinding p1);
}
