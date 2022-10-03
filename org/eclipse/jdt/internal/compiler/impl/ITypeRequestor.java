package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;

public interface ITypeRequestor
{
    void accept(final IBinaryType p0, final PackageBinding p1, final AccessRestriction p2);
    
    void accept(final ICompilationUnit p0, final AccessRestriction p1);
    
    void accept(final ISourceType[] p0, final PackageBinding p1, final AccessRestriction p2);
}
