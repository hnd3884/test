package jdk.nashorn.internal.codegen;

public class CompilationException extends RuntimeException
{
    CompilationException(final String description) {
        super(description);
    }
    
    CompilationException(final Exception cause) {
        super(cause);
    }
}
