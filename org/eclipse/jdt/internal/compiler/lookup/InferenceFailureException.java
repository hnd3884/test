package org.eclipse.jdt.internal.compiler.lookup;

public class InferenceFailureException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public InferenceFailureException(final String message) {
        super(message);
    }
}