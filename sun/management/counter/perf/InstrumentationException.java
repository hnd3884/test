package sun.management.counter.perf;

public class InstrumentationException extends RuntimeException
{
    private static final long serialVersionUID = 8060117844393922797L;
    
    public InstrumentationException() {
    }
    
    public InstrumentationException(final String s) {
        super(s);
    }
}
