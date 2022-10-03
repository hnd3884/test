package sun.net.www.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class HttpCaptureOutputStream extends FilterOutputStream
{
    private HttpCapture capture;
    
    public HttpCaptureOutputStream(final OutputStream outputStream, final HttpCapture capture) {
        super(outputStream);
        this.capture = null;
        this.capture = capture;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.capture.sent(n);
        this.out.write(n);
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        for (int length = array.length, i = 0; i < length; ++i) {
            this.capture.sent(array[i]);
        }
        this.out.write(array);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        for (int i = n; i < n2; ++i) {
            this.capture.sent(array[i]);
        }
        this.out.write(array, n, n2);
    }
    
    @Override
    public void flush() throws IOException {
        try {
            this.capture.flush();
        }
        catch (final IOException ex) {}
        super.flush();
    }
}
