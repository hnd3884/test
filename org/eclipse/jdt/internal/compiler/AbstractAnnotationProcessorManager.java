package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import java.io.PrintWriter;

public abstract class AbstractAnnotationProcessorManager
{
    public abstract void configure(final Object p0, final String[] p1);
    
    public abstract void configureFromPlatform(final Compiler p0, final Object p1, final Object p2);
    
    public abstract void setOut(final PrintWriter p0);
    
    public abstract void setErr(final PrintWriter p0);
    
    public abstract ICompilationUnit[] getNewUnits();
    
    public abstract ReferenceBinding[] getNewClassFiles();
    
    public abstract ICompilationUnit[] getDeletedUnits();
    
    public abstract void reset();
    
    public abstract void processAnnotations(final CompilationUnitDeclaration[] p0, final ReferenceBinding[] p1, final boolean p2);
    
    public abstract void setProcessors(final Object[] p0);
}
