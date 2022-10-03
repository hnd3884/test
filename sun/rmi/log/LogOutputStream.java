package sun.rmi.log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream
{
    private RandomAccessFile raf;
    
    public LogOutputStream(final RandomAccessFile raf) throws IOException {
        this.raf = raf;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.raf.write(n);
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.raf.write(array);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.raf.write(array, n, n2);
    }
    
    @Override
    public final void close() throws IOException {
    }
}
