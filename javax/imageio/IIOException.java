package javax.imageio;

import java.io.IOException;

public class IIOException extends IOException
{
    public IIOException(final String s) {
        super(s);
    }
    
    public IIOException(final String s, final Throwable t) {
        super(s);
        this.initCause(t);
    }
}
