package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public interface ReferenceContext
{
    void abort(final int p0, final CategorizedProblem p1);
    
    CompilationResult compilationResult();
    
    CompilationUnitDeclaration getCompilationUnitDeclaration();
    
    boolean hasErrors();
    
    void tagAsHavingErrors();
    
    void tagAsHavingIgnoredMandatoryErrors(final int p0);
}
