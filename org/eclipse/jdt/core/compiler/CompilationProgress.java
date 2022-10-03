package org.eclipse.jdt.core.compiler;

public abstract class CompilationProgress
{
    public abstract void begin(final int p0);
    
    public abstract void done();
    
    public abstract boolean isCanceled();
    
    public abstract void setTaskName(final String p0);
    
    public abstract void worked(final int p0, final int p1);
}
