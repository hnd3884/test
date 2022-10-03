package java.awt.print;

import java.io.IOException;

public class PrinterIOException extends PrinterException
{
    static final long serialVersionUID = 5850870712125932846L;
    private IOException mException;
    
    public PrinterIOException(final IOException mException) {
        this.initCause(null);
        this.mException = mException;
    }
    
    public IOException getIOException() {
        return this.mException;
    }
    
    @Override
    public Throwable getCause() {
        return this.mException;
    }
}
