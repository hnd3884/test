package org.eclipse.jdt.internal.compiler.env;

public interface INameEnvironment
{
    NameEnvironmentAnswer findType(final char[][] p0);
    
    NameEnvironmentAnswer findType(final char[] p0, final char[][] p1);
    
    boolean isPackage(final char[][] p0, final char[] p1);
    
    void cleanup();
}
