package org.eclipse.jdt.internal.compiler.problem;

public class ShouldNotImplement extends RuntimeException
{
    private static final long serialVersionUID = 2669970476264283736L;
    
    public ShouldNotImplement(final String message) {
        super(message);
    }
}
