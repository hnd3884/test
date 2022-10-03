package org.eclipse.jdt.core.compiler;

public class InvalidInputException extends Exception
{
    private static final long serialVersionUID = 2909732853499731592L;
    
    public InvalidInputException() {
    }
    
    public InvalidInputException(final String message) {
        super(message);
    }
}
