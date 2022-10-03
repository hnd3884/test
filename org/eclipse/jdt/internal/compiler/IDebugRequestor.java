package org.eclipse.jdt.internal.compiler;

public interface IDebugRequestor
{
    void acceptDebugResult(final CompilationResult p0);
    
    boolean isActive();
    
    void activate();
    
    void deactivate();
    
    void reset();
}
