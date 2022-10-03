package javax.print;

import java.io.OutputStream;

public abstract class StreamPrintService implements PrintService
{
    private OutputStream outStream;
    private boolean disposed;
    
    private StreamPrintService() {
        this.disposed = false;
    }
    
    protected StreamPrintService(final OutputStream outStream) {
        this.disposed = false;
        this.outStream = outStream;
    }
    
    public OutputStream getOutputStream() {
        return this.outStream;
    }
    
    public abstract String getOutputFormat();
    
    public void dispose() {
        this.disposed = true;
    }
    
    public boolean isDisposed() {
        return this.disposed;
    }
}
